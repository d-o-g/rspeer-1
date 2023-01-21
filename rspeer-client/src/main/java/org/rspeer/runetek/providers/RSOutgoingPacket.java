package org.rspeer.runetek.providers;

public interface RSOutgoingPacket extends RSProvider {

    RSOutgoingPacketMeta getMeta();

    RSPacketBuffer getBuffer();

    int getSize();

    int getPayloadSize();
}
