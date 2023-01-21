package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.GameStateEvent;

public interface GameStateListener extends EventListener {
    void notify(GameStateEvent event);
}
