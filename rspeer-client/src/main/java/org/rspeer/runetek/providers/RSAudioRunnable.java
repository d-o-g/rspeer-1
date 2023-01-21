package org.rspeer.runetek.providers;

public interface RSAudioRunnable extends RSProvider {
    void run();
	RSAudioSystem[] getSystems();
}