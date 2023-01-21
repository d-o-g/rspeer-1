package org.rspeer.script;

import java.util.Objects;

/**
 * Created by Spencer on 31/01/2018.
 */
public final class GameAccount {

    private final String username, password;
    private final int pin;
    private int dismiss;
    private String xpPreference;

    public GameAccount(String username, String password) {
        this(username, password, -1, 0, "None");
    }

    public GameAccount(String username, String password, int pin) {
        this(username, password, pin, 0, "None");
    }

    public GameAccount(String username, String password, int pin, int dismiss) {
        this(username, password, pin, dismiss, "None");
    }

    public GameAccount(String username, String password, int pin, int dismiss, String xpPreference) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.pin = pin;
        this.dismiss = dismiss;
        this.xpPreference = xpPreference;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPin() {
        return pin;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameAccount)) {
            return false;
        }
        GameAccount other = (GameAccount) o;
        return other.username.equals(username) && other.password.equals(password);
    }

    public boolean validate() {
        return !username.isEmpty() && !password.isEmpty();
    }

    @Override
    public String toString() {
        return username;
    }

    public int getDismiss() {
        return dismiss;
    }

    public String getXpPreference() {
        return xpPreference;
    }

    public void fix() {
        if (dismiss < 0 || dismiss > 100) {
            dismiss = 0;
        }

        if (xpPreference == null) {
            xpPreference = "None";
        }
    }
}
