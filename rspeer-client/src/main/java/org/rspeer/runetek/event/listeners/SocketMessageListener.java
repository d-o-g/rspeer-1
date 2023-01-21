package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;

public interface SocketMessageListener extends EventListener {
    void notify(String message);
}
