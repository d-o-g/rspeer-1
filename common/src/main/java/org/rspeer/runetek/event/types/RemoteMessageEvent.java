package org.rspeer.runetek.event.types;

import org.rspeer.entities.RemoteMessage;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.RemoteMessageListener;

public class RemoteMessageEvent extends Event<RemoteMessage> {

    public RemoteMessageEvent(RemoteMessage source, long delay) {
        super(source, delay);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof RemoteMessageListener) {
            ((RemoteMessageListener) listener).notify(this);
        }
    }
}
