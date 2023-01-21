package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.SocketMessageListener;

public final class SocketMessageEvent extends Event {

    public SocketMessageEvent(Object source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof SocketMessageListener) {
            ((SocketMessageListener) listener).notify(this.source.toString());
        }
    }
}
