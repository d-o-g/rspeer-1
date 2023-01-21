package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.WorldChangeEvent;

public interface WorldChangeListener extends EventListener {
    void notify(WorldChangeEvent event);
}
