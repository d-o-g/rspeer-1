package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.RemoteMessageEvent;

public interface RemoteMessageListener extends EventListener {
    void notify(RemoteMessageEvent e);
}
