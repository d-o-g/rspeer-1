package org.rspeer.runetek.providers;

public interface RSAudioInstrument extends RSProvider {
    RSAudioEnvelope getPitchEnvelope();
	int[] getOscillatorPitch();
}