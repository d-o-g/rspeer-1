package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.GameStateListener;

public final class GameStateEvent extends Event {

    private final int old, new_;

    public GameStateEvent(int old, int new_) {
        super("Static");
        this.old = old;
        this.new_ = new_;
    }

    public int getOld() {
        return old;
    }

    public int getNew() {
        return new_;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof GameStateListener) {
            ((GameStateListener) listener).notify(this);
        }
    }
}
