package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.TickListener;

public final class TickEvent extends Event<TickEvent.Type> {

    public TickEvent(Type source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof TickListener) {
            ((TickListener) listener).notify(this);
        }
    }

    public enum Type {
        ENGINE, SERVER;
    }
}
