package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.OutgoingPacketEvent;

public interface OutgoingPacketListener extends EventListener {
    void notify(OutgoingPacketEvent event);
}
