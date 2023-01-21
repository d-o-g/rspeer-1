package org.rspeer.runetek.providers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

public interface RSAudioSystemImpl extends RSAudioSystem {
    int getBufferSize();
	AudioFormat getFormat();
	SourceDataLine getSourceDataLine();
	byte[] getBuffer();
}