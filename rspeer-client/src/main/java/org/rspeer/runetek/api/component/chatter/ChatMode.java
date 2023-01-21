package org.rspeer.runetek.api.component.chatter;

import org.rspeer.runetek.api.Game;

/**
 * An enum containing possible settings for the different chat modes
 * @see Game#getPublicChatMode
 * @see Game#getTradeChatMode()
 */
public enum ChatMode {

    ON(0),
    AUTOCHAT(4),
    FRIENDS(1),
    OFF(2),
    HIDDEN(3);

    private final int id;

    ChatMode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
