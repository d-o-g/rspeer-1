package org.rspeer.api_services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import net.jodah.failsafe.function.CheckedRunnable;
import org.rspeer.api_services.encypt.EncryptUtil;
import org.rspeer.api_services.enitites.ClientInfo;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.entities.RemoteMessage;
import org.rspeer.runetek.event.EventDispatcherProvider;
import org.rspeer.runetek.event.types.RemoteMessageEvent;
import org.rspeer.runetek.event.types.ServerConnectionEvent;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PingService {

    private static PingService instance;
    private boolean showingDialog;
    private boolean isClosing;
    private ScheduledFuture<?> shutdownFuture;
    private boolean registered;
    private String session;
    private long lastUpdate = System.currentTimeMillis();
    private Callable<ClientInfo> clientInfoAggregator;
    private PingService() {
        this.session = AuthorizationService.getInstance().decryptAndGetSession();
    }

    public static PingService getInstance() {
        if (instance == null) {
            instance = new PingService();
        }
        return instance;
    }

    private static void openBrowser(String url) {
        JOptionPane pane = new JOptionPane("Attempting to open " + url + " in the browser.", JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog("Opening Browser");
        RsPeerExecutor.schedule(() -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                showError("Failed to open " + url + " in the browser.");
                e.printStackTrace();
            }
        }, 1, TimeUnit.SECONDS);
        RsPeerExecutor.schedule(() -> dialog.setVisible(false), 3, TimeUnit.SECONDS);
        dialog.setVisible(true);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isRegistered() {
        return registered;
    }

    public synchronized void start(Callable<ClientInfo> clientInfoAggregator) {
        this.clientInfoAggregator = clientInfoAggregator;
        RsPeerExecutor.scheduleAtFixedRate(() -> ping(false, false), 0, 20, TimeUnit.SECONDS);
        RsPeerExecutor.scheduleAtFixedRate(this::checkExpired, 0, 1, TimeUnit.MINUTES);
    }

    public synchronized void onClientClose() {
        ping(true, false);
    }

    public synchronized void onBan() {
        CheckedRunnable runnable = getRunnable(false, true);
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.getInstance().capture(e);
        }
    }

    public synchronized void ping() {
        ping(false, false);
    }

    public synchronized void ping(boolean isClose, boolean isBanned) {
        CheckedRunnable runnable = getRunnable(isClose, isBanned);
        if (isClose) {
            try {
                runnable.run();
            } catch (Exception e) {
                Logger.getInstance().capture(e);
            }
        } else {
            RsPeerExecutor.execute(runnable);
        }
    }

    private synchronized CheckedRunnable getRunnable(boolean isClose, boolean isBanned) {
        return () -> {
            try {
                if (session == null) {
                    session = RsPeerApi.getSession();
                }
                if (session == null) {
                    return;
                }
                String route = isClose ? "close" : "update";
                String identifier = RsPeerApi.getIdentifier();
                HttpRequestWithBody request = Unirest
                        .post(Configuration.NEW_API_BASE + "client/" + route + "?clientId=" + identifier)
                        .header("Authorization", "Bearer " + session);

                ClientInfo info = null;

                if (clientInfoAggregator != null) {
                    try {
                        info = clientInfoAggregator.call();
                        info.setBanned(isBanned);
                        String gson = RsPeerApi.gson.toJson(info);
                        byte[] xor = EncryptUtil.xor(gson.getBytes(), "570cc96145b1d0cde3f60ce0dab7ba9b");
                        String base64 = Base64.getEncoder().encodeToString(xor);
                        if (base64.length() > 0) {
                            request.body(base64);
                        }
                    } catch (Throwable ignored) {
                    }
                }

                HttpResponse<String> response = request.asString();

                if (response.getStatus() != 200) {
                    RemoteMessage message = new RemoteMessage();
                    message.setSource("log:critical");
                    if (!response.getBody().contains("over your instance limit")) {
                        message.setMessage("Received bad request response from server. Response: " + response.getBody());
                        RsPeerApi.log("rspeer_client_07:ping:error", RsPeerApi.getIdentifier() + " - " + response.getStatus() + ":" + response.getBody(), 5);
                    } else {
                        registered = true;
                        message.setMessage("You are currently over your instance limit, your client may close in 5 minutes if this or other clients are not closed.");
                    }
                    EventDispatcherProvider.provide().immediate(
                            new RemoteMessageEvent(message, 0));
                    return;
                }

                registered = true;

                if (info == null) {
                    lastUpdate = System.currentTimeMillis();
                    return;
                }

                String body = response.getBody().replace("\"", "");
                byte[] decoded = Base64.getDecoder().decode(body);
                byte[] xor = EncryptUtil.xor(decoded, "570cc96145b1d0cde3f60ce0dab7ba9b");
                String hash = new String(xor);
                String generated = generateHash(info);
                hash = new String(Base64.getDecoder().decode(hash)).toLowerCase();
                hash = Base64.getEncoder().encodeToString(hash.getBytes());

                if (!generated.equals(hash)) {
                    RsPeerApi.log("rspeer_client_07:ping:hash_mismatch", RsPeerApi.getIdentifier() + " - " + body + " - " + generated, 5);
                    return;
                }

                lastUpdate = System.currentTimeMillis();
                isClosing = false;
                EventDispatcherProvider.provide().immediate(
                        new ServerConnectionEvent(new Object(), ServerConnectionEvent.ServerConnectionStatus.CONNECTED));

            } catch (Throwable e) {
                RsPeerApi.log("rspeer_client_07:ping:error", RsPeerApi.getIdentifier() + " - " + e.toString(), 5);
            }
        };
    }

    private void checkExpired() {
        if (isExpired()) {
            overLimit("Your client has lost connection to the server for over 30 minutes. This can happen if you are over your instance limit. " +
                    "Your client will be closed in 30 minutes unless connection is regained.");
            EventDispatcherProvider.provide().immediate(
                    new ServerConnectionEvent(new Object(), ServerConnectionEvent.ServerConnectionStatus.DISCONNECTED));
        }
    }

    private boolean isExpired() {
        long diff = System.currentTimeMillis() - lastUpdate;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        return minutes > 29;
    }

    private void overLimit(String reason) {
        if (showingDialog || isClosing) {
            return;
        }
        shutdown();
        RsPeerExecutor.execute(() -> EventQueue.invokeLater(() -> showOverLimitPopup(reason)));
    }

    private void shutdown() {
        shutdownFuture = RsPeerExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!isExpired()) {
                    isClosing = false;
                    shutdownFuture.cancel(true);
                    return;
                }
                RsPeerApi.shutDown();
                PingService.getInstance().onClientClose();
                Runtime.getRuntime().exit(1);
            } catch (Throwable e) {
                Runtime.getRuntime().exit(1);
            }
        }, 30, 5, TimeUnit.MINUTES);
    }

    private void showOverLimitPopup(String message) {
        try {
            showingDialog = true;
            Optional<String> purchaseUrl = SiteConfigService.getString("instances_purchase:url");
            Optional<String> moreInfoUrl = SiteConfigService.getString("instances_more_info:url");
            String[] options = new String[]{"OK", "More Info", "Purchase Instances"};
            int response = JOptionPane.showOptionDialog(null, message, "Out Of Instances",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);
            if (response == 2) {
                openBrowser(purchaseUrl.orElse("https://rspeer.org"));
            }
            if (response == 1) {
                openBrowser(moreInfoUrl.orElse("https://rspeer.org"));
            }
            isClosing = true;
        } finally {
            showingDialog = false;
        }
    }

    private String clean(String value) {
        if (value != null) {
            value = value.trim();
            return value;
        }
        return "";
    }

    private String generateHash(ClientInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(RsPeerApi.getIdentifier())
                .append(":").append(clean(info.getRunescapeLogin()))
                .append(":").append(clean(info.getScriptName()))
                .append(":").append(clean(info.getIpAddress()))
                .append(":").append(clean(info.getProxyIp()))
                .append(":").append(info.getOperatingSystem())
                .append(":").append(info.isBanned());
        return Base64.getEncoder().encodeToString(builder.toString().toLowerCase().getBytes());
    }
}
