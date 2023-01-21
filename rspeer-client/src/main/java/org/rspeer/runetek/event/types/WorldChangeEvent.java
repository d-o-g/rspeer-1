package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.WorldChangeListener;
import org.rspeer.runetek.providers.RSWorld;

public final class WorldChangeEvent extends Event<RSWorld> {

    private final RSWorld new_;

    public WorldChangeEvent(RSWorld old, RSWorld new_) {
        super(old);
        this.new_ = new_;
    }

    public RSWorld getOld() {
        return getSource();
    }

    public RSWorld getNew() {
        return new_;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof WorldChangeListener) {
            ((WorldChangeListener) listener).notify(this);
        }
    }
}
