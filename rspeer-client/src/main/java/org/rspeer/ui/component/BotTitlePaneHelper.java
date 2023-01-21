package org.rspeer.ui.component;

import org.rspeer.RSPeer;
import org.rspeer.api_services.Logger;
import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.HttpUtil;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptExecutor;

import static org.rspeer.commons.Configuration.APPLICATION_NAME;

public final class BotTitlePaneHelper {

    private static final Object lock = new Object();
    private static String ip;

    public static String getFrameTitle(boolean withIp) {
        synchronized (lock) {
            try {
                BotPreferences prefs = BotPreferences.getInstance();
                StringBuilder builder = new StringBuilder(APPLICATION_NAME);
                if(prefs.isShowAccountOnMenuBar()) {
                    GameAccount account = RSPeer.getGameAccount();
                    if(account != null) {
                        String rsn = Players.getLocal() != null ? Players.getLocal().getName() : "";
                        builder.append(" | ").append(account.getUsername());
                        if(rsn != null && rsn.length() > 0) {
                            builder.append(" ").append("(").append(rsn).append(")");
                        }
                    }
                }
                if(prefs.isShowScriptOnMenuBar()) {
                    Script script = ScriptExecutor.getCurrent();
                    if(script != null) {
                        builder.append(" | ").append(script.getMeta().name());
                    }
                }
                if(prefs.isAllowScriptMessageOnMenuBar()) {
                    Script script = ScriptExecutor.getCurrent();
                    if(script != null && script.getTitlePaneMessage() != null && script.getTitlePaneMessage().length() > 0) {
                        builder.append(" | ").append(script.getTitlePaneMessage());
                    }
                }
                if (!withIp) {
                    return builder.toString();
                }
                if (ip == null) {
                    ip = HttpUtil.getIpAddress();
                }
                builder.append(" | ").append(ip);
                return builder.toString();
            } catch (Exception e) {
                Logger.getInstance().capture(e);
                return APPLICATION_NAME;
            }
        }
    }

    public static void refreshFrameTitle() {
        synchronized (lock) {
            RSPeer.getView().setTitle(BotTitlePaneHelper.getFrameTitle(BotPreferences.getInstance().isShowIpOnMenuBar()));
        }
    }
}