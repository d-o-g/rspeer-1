package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ServerConnectionEvent;

public interface ServerConnectionChangeListener extends EventListener {
    void notify(ServerConnectionEvent e);
}
