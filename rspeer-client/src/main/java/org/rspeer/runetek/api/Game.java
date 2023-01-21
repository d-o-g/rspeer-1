package org.rspeer.runetek.api;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.AccountType;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.chatter.ChatMode;
import org.rspeer.runetek.event.EventDispatcher;
import org.rspeer.runetek.event.EventMediator;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSClientPreferences;

import java.awt.*;
import java.util.function.Predicate;

public final class Game {

    public static final int STATE_LOADING_GAME = 5;
    public static final int STATE_CREDENTIALS_SCREEN = 10;
    public static final int STATE_LOGGING_IN = 20;
    public static final int STATE_LOADING_REGION = 25;
    public static final int STATE_IN_GAME = 30;
    public static final int STATE_CONNECTION_LOST = 40;
    public static final int STATE_HOPPING_WORLD = 45;

    private static final Predicate<InterfaceComponent> LOGOUT_PREDICATE = x -> x.containsAction("Logout");

    private Game() {
        throw new IllegalAccessError();
    }

    public static RSClient getClient() {
        return ClientSupplier.get();
    }

    public static RSClientPreferences getClientPreferences() {
        return getClient().getPreferences();
    }

    /**
     * @return The current in-game state
     */
    public static int getState() {
        return getClient().getGameState();
    }

    /**
     * @return {@code true} if logged into the game
     */
    public static boolean isLoggedIn() {
        int state = getState();
        return state == STATE_IN_GAME || state == STATE_LOADING_REGION;
    }

    /**
     * @return {@code true} if on the login screen
     */
    public static boolean isOnCredentialsScreen() {
        return getState() == STATE_CREDENTIALS_SCREEN;
    }

    /**
     * @return {@code true} if the game is loading a region
     */
    public static boolean isLoadingRegion() {
        return getState() == STATE_LOADING_REGION;
    }

    /**
     * @return The current engine cycle
     */
    public static int getEngineCycle() {
        return getClient().getEngineCycle();
    }

    /**
     * @return The current game canvas
     */
    public static Canvas getCanvas() {
        return getClient().getCanvas();
    }

    /**
     * @return The current public chat mode
     */
    public static ChatMode getPublicChatMode() {
        int id = Game.getClient().getPublicChatMode();
        for (ChatMode mode : ChatMode.values()) {
            if (mode.getId() == id) {
                return mode;
            }
        }
        throw new IllegalStateException("Unknown ChatMode state: " + id);
    }

    /**
     * @return The current trade chat mode
     */
    public static ChatMode getTradeChatMode() {
        int id = Game.getClient().getTradeChatMode();
        for (ChatMode mode : ChatMode.values()) {
            if (mode.getId() == id) {
                return mode;
            }
        }
        throw new IllegalStateException("Unknown ChatMode state: " + id);
    }

    /**
     * @return The {@link EventDispatcher} for events fired by the game client
     */
    public static EventDispatcher getEventDispatcher() {
        return getClient().getEventDispatcher();
    }

    /**
     * @return The {@link EventMediator} for events fired by the game client
     */
    public static EventMediator getEventMediator() {
        return getClient().getEventMediator();
    }

    /**
     * @see AccountType
     * @return The current account type
     */
    public static AccountType getAccountType() {
        int index = Varps.getBitValue(1777);
        AccountType[] types = AccountType.values();
        return index >= 1 && index <= 3 ? types[index - 1] : AccountType.NORMAL;
    }

    /**
     * @return Interacts with the logout button and returns {@code true} on successful interaction
     */
    public static boolean logout() {
        InterfaceComponent logout = Interfaces.getFirst(InterfaceComposite.LOGOUT_TAB.getGroup(), LOGOUT_PREDICATE);
        if (logout == null) {
            logout = Interfaces.getFirst(InterfaceComposite.WORLD_SELECT.getGroup(), LOGOUT_PREDICATE);
        }
        return logout != null && logout.interact("Logout");
    }


    /**
     * @return {@code true} if you are currently in a cutscene
     */
    public static boolean isInCutscene() {
        return Varps.getBitValue(542) == 1
                && Varps.getBitValue(4606) == 1;
    }

    /**
     * @return The remaining membership days for the current account
     */
    public static int getRemainingMembershipDays() {
        return Varps.get(1780);
    }
}
