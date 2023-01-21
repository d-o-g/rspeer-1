package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ChatMessageListener;

public final class ChatMessageEvent extends Event<String> {

    private final String message;
    private final ChatMessageType type;
    private final String channel;

    public ChatMessageEvent(String source, String message, String channel, int type) {
        super(source);
        this.message = message;
        this.channel = channel;
        this.type = ChatMessageType.lookup(type);
    }

    public String getMessage() {
        return message;
    }

    public ChatMessageType getType() {
        return type;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ChatMessageListener) {
            ((ChatMessageListener) listener).notify(this);
        }
    }
}
