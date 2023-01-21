package org.rspeer.runetek.providers;

public interface RSOutgoingPacketMeta extends RSProvider {

    int getOpcode();

    int getSize();
}
