package org.rspeer.ui;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.rspeer.api_services.PingService;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.event.EventDispatcherProvider;
import org.rspeer.runetek.event.listeners.MouseInputListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//TODO fade in/out
public final class BotAd implements RenderListener, MouseInputListener {

    private static final Dimension SIZE = new Dimension(750, 120);
    private static final Dimension DISMISS_SIZE = new Dimension(25, 25);

    private static Duration DELAY = Duration.ofMinutes(2);
    private StopWatch showing;

    private Image image;
    private Image closeImage;
    private Rectangle imageBounds;
    private Rectangle dismissBounds;

    private ScheduledFuture<?> future;

    private ListIterator<BotAdEntry> adsIterator;
    private List<BotAdEntry> ads;
    private BotAdEntry current;
    private boolean initialized;

    //initial delay
    private Instant closedAt = Instant.now().minusSeconds(DELAY.getSeconds() - Random.nextInt(1));

    public BotAd() {
        EventDispatcherProvider.provide().register(this);
        future = RsPeerExecutor.scheduleAtFixedRate(this::init, 0, 5, TimeUnit.SECONDS);
        try {
            closeImage = ImageIO.read(BotAd.class.getResourceAsStream("close-red.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void init() {
        if (initialized) {
            if (future != null) {
                future.cancel(true);
            }
            return;
        }

        if(!PingService.getInstance().isRegistered()) {
            return;
        }

        try {
            HttpResponse<String> result = Unirest.get(Configuration.NEW_API_BASE + "botAd/get?tag=" + RsPeerApi.getIdentifier())
                    .header("Authorization", "Bearer " + RsPeerApi.getSession()).asString();

            if (result.getStatus() != 200) {
                return;
            }


            String body = result.getBody();

            BotAdResult adResult = RsPeerApi.gson.fromJson(body, BotAdResult.class);
            boolean shouldShow = adResult.isShouldShow();
            if (!shouldShow) {
                initialized = true;
                return;
            }

            BotAd.DELAY = Duration.ofSeconds(adResult.getHideSeconds());
            this.ads = adResult.getAds();
            this.adsIterator = ads.listIterator();
            startImageSwitcher();
            initialized = true;

        } catch (Exception ignored) {
        }
    }

    private void startImageSwitcher() {
        RsPeerExecutor.scheduleAtFixedRate(() -> {
            if(isClosed()) {
                return;
            }
            if(showing != null && showing.isRunning()) {
                return;
            }
            setNextImage();
        }, 3, 5, TimeUnit.SECONDS);
    }

    private void setNextImage() {
        if (!initialized || adsIterator == null) {
            return;
        }
        if (!adsIterator.hasNext()) {
            adsIterator = ads.listIterator();
        }
        current = adsIterator.next();
        Image image = null;
        try {
            image = new ImageIcon(new URL(current.getImage())).getImage();
        } catch (Exception ignored) {
        }

        if (image == null) {
            dismissBounds = new Rectangle();
            imageBounds = new Rectangle();
            return;
        }

        this.image = image;

        imageBounds = new Rectangle((765 - SIZE.width) / 2, 500 - SIZE.height, SIZE.width, SIZE.height);
        dismissBounds = new Rectangle(
                (int) (SIZE.width - (DISMISS_SIZE.width * 1.45)),
                500 - SIZE.height,
                SIZE.width,
                SIZE.height
        );

        showing = StopWatch.fixed(Duration.ofSeconds(20));
    }

    @Override
    public void notify(RenderEvent event) {
        if (!isActive()) {
            return;
        }

        Graphics2D g = (Graphics2D) event.getSource();

        g.drawImage(image, imageBounds.x, imageBounds.y, SIZE.width, SIZE.height, null);
        g.drawImage(closeImage, dismissBounds.x, dismissBounds.y, null);
    }

    private boolean isActive() {
        return current != null && image != null && !isClosed();
    }

    private boolean isClosed() {
        return closedAt != null && closedAt.plusSeconds(DELAY.getSeconds()).isAfter(Instant.now());
    }

    @Override
    public void notify(MouseEvent event) {
        if (event.getSource().equals("bot")
                || !isActive()
                || event.getID() != MouseEvent.MOUSE_PRESSED
                || !SwingUtilities.isLeftMouseButton(event)) {
            return;
        }

        Point point = event.getPoint();
        if (dismissBounds.contains(point)) {
            closedAt = Instant.now();
            RsPeerExecutor.execute(this::setNextImage);
        } else if (imageBounds.contains(point)) {
            try {
                Desktop.getDesktop().browse(new URL(current.getRedirect()).toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}