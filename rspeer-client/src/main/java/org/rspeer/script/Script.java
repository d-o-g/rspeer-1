package org.rspeer.script;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.network.RSPeerUser;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.script.events.BankPin;
import org.rspeer.script.events.DismissRandoms;
import org.rspeer.script.events.LoginScreen;
import org.rspeer.script.events.WelcomeScreen;
import org.rspeer.script.events.breaking.BreakEvent;
import org.rspeer.script.events.breaking.BreakProfile;
import org.rspeer.script.events.genie.GenieSolver;
import org.rspeer.ui.Log;
import org.rspeer.ui.component.BotTitlePaneHelper;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Script extends LoopTask {

    private final List<ScriptBlockingEvent> blockingEvents = new CopyOnWriteArrayList<>();

    private String args;
    private BreakProfile profile;
    private BreakEvent breakEvent;
    private String titlePaneMessage;
    private ScriptConfiguration configuration;

    public Script() {
        configuration = new ScriptConfiguration();
    }

    public static Path getDataDirectory() {
        return Paths.get(Configuration.DATA);
    }

    public static RSPeerUser getRSPeerUser() {
        JSONObject user = RsPeerApi.getCurrentUser();
        String username = user.getString("username");
        JSONArray groups = user.getJSONArray("groupNames");
        HashSet<String> names = new HashSet<>();
        for (Object group : groups) {
            names.add(group.toString());
        }
        return new RSPeerUser(username, names);
    }

    public final String getTitlePaneMessage() {
        return titlePaneMessage;
    }

    public final void setTitlePaneMessage(String message) {
        this.titlePaneMessage = message;
        if (BotPreferences.getInstance().isAllowScriptMessageOnMenuBar()) {
            RsPeerExecutor.execute(BotTitlePaneHelper::refreshFrameTitle);
        }
    }

    @Deprecated
    public void setMenuBarMessage(String menuBarMessage) {
       setTitlePaneMessage(menuBarMessage);
    }

    @Deprecated
    public String getMenuBarMessage() {
        return getTitlePaneMessage();
    }

    public final void start() {
        try {
            blockingEvents.add((breakEvent = new BreakEvent(this, profile)));
            BreakEvent.setCondition(BreakEvent.getDefaultCondition());
            blockingEvents.add(new LoginScreen(this));
            blockingEvents.add(new WelcomeScreen(this));
            blockingEvents.add(new BankPin(this));
            blockingEvents.add(new GenieSolver(this));
            blockingEvents.add(new DismissRandoms(this));

            GameAccount ga = RSPeer.getGameAccount();
            if (ga == null) {
                String user = Game.getClient().getUsername();
                if (user != null && !user.isEmpty()) {
                    RSPeer.setGameAccount(new GameAccount(user, Game.getClient().getPassword()));
                }
            }

            for (ScriptBlockingEvent event : blockingEvents) {
                Game.getEventDispatcher().register(event);
            }

            onStart();
            super.start();
        } catch (Exception e) {
            if (!BotPreferences.getInstance().isExpandLogger()) {
                Log.setMinified(false);
            }
            Log.severe(e);
        }
    }

    public void onStart() {
    }

    @Override
    public final void run() {
        if (this instanceof EventListener) {
            Game.getEventDispatcher().register((EventListener) this);
        }

        super.run();

        if (this instanceof EventListener) {
            Game.getEventDispatcher().deregister((EventListener) this);
        }
    }

    public void setBreakProfile(BreakProfile profile) {
        this.profile = profile;
        if (breakEvent != null) {
            blockingEvents.remove(breakEvent);
            blockingEvents.add(new BreakEvent(this, profile));
        }
    }

    public BreakProfile getProfile() {
        return profile;
    }

    public final GameAccount getAccount() {
        return RSPeer.getGameAccount();
    }

    public final void setAccount(GameAccount account) {
        RSPeer.setGameAccount(account);
    }

    public void addBlockingEvent(ScriptBlockingEvent event) {
        blockingEvents.add(0, event);
        Game.getEventDispatcher().register(event);
    }

    public <T extends ScriptBlockingEvent> T getBlockingEvent(Class<T> clazz) {
        for (ScriptBlockingEvent e : blockingEvents) {
            if (e.getClass() == clazz) {
                return (T) e;
            }
        }
        return null;
    }

    public void removeBlockingEvent(Class<? extends ScriptBlockingEvent> clazz) {
        for (ScriptBlockingEvent event : blockingEvents) {
            if (event.getClass() == clazz) {
                blockingEvents.remove(event);
                Game.getEventDispatcher().deregister(event);
            }
        }
    }

    @Override
    protected final void processBlockingEvents() {
        for (ScriptBlockingEvent e : blockingEvents) {
            if (e.validate()) {
                e.togglePaint(true);
                e.process();
                return;
            }
        }
    }

    @Override
    protected final boolean pendingBlockingEvents() {
        for (ScriptBlockingEvent e : blockingEvents) {
            if (e.validate()) {
                return true;
            } else {
                e.togglePaint(false);
            }
        }
        return false;
    }

    public void setStopping(boolean stopping) {
        super.setStopping(stopping);
        if (stopping) {
            for (ScriptBlockingEvent e : blockingEvents) {
                if (Game.getEventDispatcher().isRegistered(e)) {
                    Game.getEventDispatcher().deregister(e);
                }
            }
        }
    }

    @Deprecated
    public void registerUi(JFrame jFrame) {
        registerUi(jFrame, false);
    }

    @Deprecated
    public void registerUi(JFrame jFrame, boolean blockUntilClosed) {
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public final ScriptMeta getMeta() {
        return getClass().getAnnotation(ScriptMeta.class);
    }

    public ScriptConfiguration getConfiguration() {
        return configuration;
    }
}