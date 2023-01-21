package org.rspeer.runetek.providers;

public interface RSConnectionContext extends RSProvider {

    RSIsaacCipher getEncryptor();

    RSBuffer getOutgoing();

    RSPacketBuffer getBuffer();

    RSLinkedList getPackets();

    int getBuffered();

    int getIdleReadPulses();

    int getIdleWritePulses();
}
