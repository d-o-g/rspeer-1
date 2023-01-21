package org.rspeer.runetek.providers;

public interface RSBuffer extends RSNode {
    int getCaret();
	byte[] getPayload();
}