package org.rspeer.runetek.api.component.chatter;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.EnterInput;
import org.rspeer.runetek.event.types.ChatMessageType;
import org.rspeer.runetek.providers.RSBefriendedPlayer;
import org.rspeer.runetek.providers.RSChatstream;

import java.util.function.Predicate;

/**
 * Provides functionality for sending and reading messages.
 * Note that {@link org.rspeer.runetek.event.listeners.ChatMessageListener}
 * should be used in place of this for reading where possible
 */
public final class Chat {

    private Chat() {
        throw new IllegalAccessError();
    }

    /**
     * Sends a message to public chat
     * @param message The message to send
     */
    public static boolean send(String message) {
        Game.getClient().fireScriptEvent(96, message, 0);
        return true;
    }

    /**
     * Sends a message to an added friend
     * @param friend The name of the friend to message
     * @param message The message to send
     */
    public static boolean send(String friend, String message) {
        Game.getClient().fireScriptEvent(107, friend);
        if (Time.sleepUntil(EnterInput::isOpen, 1200)) {
            Game.getClient().fireScriptEvent(682, EnterInput.Type.MESSAGE_BEFRIENDED_PLAYER, message);
            return true;
        }
        return false;
    }

    /**
     * Sends a message to an added friend
     * @param predicate A predicate matching a friend that the message should be sent to
     * @param message The message to send
     */
    public static boolean send(Predicate<RSBefriendedPlayer> predicate, String message) {
        RSBefriendedPlayer friend = BefriendedPlayers.getFirst(predicate);
        return friend != null && send(friend.getName(), message);
    }

    /**
     * Looks up a chat stream by type
     * @param type The type
     * @return A chatstream for the specified type
     */
    public static RSChatstream lookup(int type) {
        return Game.getClient().getChatstream(type);
    }
}
