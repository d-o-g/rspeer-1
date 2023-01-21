package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ServerConnectionChangeListener;

public final class ServerConnectionEvent extends Event {

    private ServerConnectionStatus status;

    public ServerConnectionEvent(Object source, ServerConnectionStatus status) {
        super(source);
        this.status = status;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ServerConnectionChangeListener) {
            ((ServerConnectionChangeListener) listener).notify(this);
        }
    }

    public ServerConnectionStatus getStatus() {
        return status;
    }

    public enum ServerConnectionStatus {
        CONNECTED, DISCONNECTED
    }
}
