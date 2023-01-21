package org.rspeer.runetek.providers;

public interface RSAudioEffect extends RSProvider {
    int getEnd();
	int getStart();
	RSAudioInstrument[] getInstruments();
}