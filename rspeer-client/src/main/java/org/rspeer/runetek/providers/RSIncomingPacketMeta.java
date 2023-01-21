package org.rspeer.runetek.providers;

public interface RSIncomingPacketMeta extends RSProvider {

    int getOpcode();

    int getSize();
}
