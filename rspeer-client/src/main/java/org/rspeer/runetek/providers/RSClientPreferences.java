package org.rspeer.runetek.providers;

import java.util.LinkedHashMap;

public interface RSClientPreferences extends RSProvider {

    default boolean isResizable() {
        return getResizable() != 1;
    }

    int getResizable();

    LinkedHashMap<Integer, Integer> getProperties();

    boolean isLoginScreenAudioDisabled();

    boolean isRememberMe();

    boolean isRoofsHidden();
}