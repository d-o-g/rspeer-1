package org.rspeer;

import com.google.gson.Gson;
import org.rspeer.api_services.PingService;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.Crawler;
import org.rspeer.commons.ProxyCommons;
import org.rspeer.runetek.api.ClientSupplier;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.event.EventDispatcherProvider;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.script.GameAccount;
import org.rspeer.ui.BotSkinManager;
import org.rspeer.ui.BotView;
import org.rspeer.ui.Log;
import org.rspeer.ui.component.BotTitlePaneHelper;

import java.awt.*;

public final class RSPeer {

    public static final Gson gson = new Gson();
    private static int defaultWorld = 36;
    private static BotView view;
    private static RSClient client;
    private static Crawler crawler;
    private static BotArgs botArgs;
    private static QuickStartArgs quickStartArgs;
    private static String originalIp;
    private static GameAccount gameAccount;
    private static final Object lock = new Object();

    static void crawl() throws Exception {
        Log.info("Crawler", "Crawling.");
        crawler = new Crawler();
        crawler.crawl();
        BotView view = getView();
        if (crawler.isOutdated()) {
            view.getSplash().setState("Downloading Game...");
            crawler.download();
        }
        view.getSplash().setState("Starting RSPeer...");
        for (int i = 1; i <= 100; i++) {
            if (i > 60) {
                view.getSplash().setState("Initializing Game...");
            } else if (i > 40) {
                view.getSplash().setState("Deflecting Game...");
            } else if (i > 20) {
                view.getSplash().setState("Loading Config...");
            }
            view.getSplash().setProgress(i);
            Time.sleep(10);
        }
    }

    public static void start() {
        BotSkinManager manager = new BotSkinManager();
        manager.initializeLookAndFeel();
        view = new BotView();
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        view.pack();
    }

    static void loadGame(ClassLoader loader) {
        try {
            String proxy = ProxyCommons.trySet(RSPeer.getBotArgs(), RSPeer.getQuickStartArgs());
            client = (RSClient) crawler.create(loader);
            ClientSupplier.set(client);
            EventQueue.invokeAndWait(() -> {
                BotView view = getView();
                view.getSplash().setState("Creating Applet");
                view.removeSplash();
                view.add((Component) client, BorderLayout.CENTER);
            });
            RSClient client = getClient();
            client.setEventMediator(EventMediatorProvider.provide());
            client.setEventDispatcher(EventDispatcherProvider.provide());
            client.asApplet().init();
            client.asApplet().start();
            RSPeer.getView().setTitle(BotTitlePaneHelper.getFrameTitle(BotPreferences.getInstance().isShowIpOnMenuBar()));
            if (proxy != null) {
                Log.fine("Successfully set proxy to: " + proxy);
            }

            RSPeer.getView().revalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameAccount getGameAccount() {
        return gameAccount;
    }

    public static void setGameAccount(GameAccount gameAccount) {
        RSPeer.gameAccount = gameAccount;
    }

    public static Crawler getCrawler() {
        return crawler;
    }

    public static RSClient getClient() {
        return ClientSupplier.get();
    }

    public static BotView getView() {
        synchronized (lock) {
            if(view == null) {
                view = new BotView();
            }
            return view;
        }
    }

    public static BotArgs getBotArgs() {
        return botArgs;
    }

    public static void setBotArgs(BotArgs botArgs) {
        RSPeer.botArgs = botArgs;
    }

    public static QuickStartArgs getQuickStartArgs() {
        return quickStartArgs;
    }

    public static void setQuickStartArgs(QuickStartArgs quickStartArgs) {
        RSPeer.quickStartArgs = quickStartArgs;
    }

    public static void setOriginalIpOnce(String originalIp) {
        if (RSPeer.originalIp != null) {
            return;
        }
        RSPeer.originalIp = originalIp;
    }

    public static int getDefaultWorld() {
        return defaultWorld;
    }

    public static void setDefaultWorld(int defaultWorld) {
        RSPeer.defaultWorld = defaultWorld;
    }

    public static String getOriginalIp() {
        return originalIp;
    }

    public static synchronized String getClientTag() {
        return RsPeerApi.getIdentifier();
    }

    public static void shutdown() {
        PingService.getInstance().onClientClose();
        RsPeerApi.shutDown();
        Runtime.getRuntime().exit(0);
    }
}
