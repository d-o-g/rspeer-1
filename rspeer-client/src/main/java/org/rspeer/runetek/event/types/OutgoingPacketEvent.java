package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.OutgoingPacketListener;
import org.rspeer.runetek.providers.RSOutgoingPacket;

public final class OutgoingPacketEvent extends Event<RSOutgoingPacket> {

    public OutgoingPacketEvent(RSOutgoingPacket source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof OutgoingPacketListener) {
            ((OutgoingPacketListener) listener).notify(this);
        }
    }
}
