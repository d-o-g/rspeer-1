package org.rspeer.runetek.api;

import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSClientPreferences;

import java.util.Collections;
import java.util.Map;

public final class Login {

    public static final int STATE_MAIN_MENU = 0;
    public static final int STATE_LEGACY = 1;
    public static final int STATE_ENTER_CREDENTIALS = 2;
    public static final int STATE_INVALID_CREDENTIALS = 3;
    public static final int STATE_AUTHENTICATOR = 4;
    public static final int STATE_FORGOT_PASSWORD = 5;
    public static final int STATE_EMPTY = 6;
    public static final int STATE_DISPLAY_ACCOUNT_STATUS = 12;

    private Login() {
        throw new IllegalAccessError();
    }

    /**
     * @return The current login state
     */
    public static int getState() {
        return Game.getClient().getLoginState();
    }

    /**
     * @return An array of response lines on the login screen
     */
    public static String[] getResponseLines() {
        RSClient c = Game.getClient();
        return new String[]{c.getLoginResponse1(), c.getLoginResponse2(), c.getLoginResponse3()};
    }

    /**
     * Skips to the enter credentials screen and enters the given details
     *
     * @param username The username to enter
     * @param password The password to enter
     */
    public static void enterCredentials(String username, String password) {
        RSClient client = Game.getClient();

        if (client.getLoginState() != STATE_ENTER_CREDENTIALS) {
            client.setLoginState(STATE_ENTER_CREDENTIALS);
        }

        if (!client.getUsername().equals(username)) {
            client.setUsername(username);
        }

        if (!client.getPassword().equals(password)) {
            client.setPassword(password);
        }
    }

    public static boolean isRememberMeEnabled() {
        return Functions.mapOrElse(() -> Game.getClient().getPreferences(), RSClientPreferences::isRememberMe);
    }

    public static boolean isAudioDisabled() {
        return Functions.mapOrElse(() -> Game.getClient().getPreferences(), RSClientPreferences::isLoginScreenAudioDisabled);
    }

    public static Map getProperties() {
        return Functions.mapOrDefault(() -> Game.getClient().getPreferences(), RSClientPreferences::getProperties, Collections.emptyMap());
    }

    public static boolean isDisplayingAccountStatus() {
        return getState() == STATE_DISPLAY_ACCOUNT_STATUS;
    }
}
