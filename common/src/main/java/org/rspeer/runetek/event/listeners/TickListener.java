package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.TickEvent;

public interface TickListener extends EventListener {
    void notify(TickEvent e);
}
