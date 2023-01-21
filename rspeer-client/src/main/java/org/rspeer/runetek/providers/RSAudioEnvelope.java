package org.rspeer.runetek.providers;

public interface RSAudioEnvelope extends RSProvider {
    int getAmplitude();
	int getEnd();
	int getMax();
	int getPhaseIndex();
	int getStart();
	int getStep();
	int getTicks();
	int[] getPhases();
}