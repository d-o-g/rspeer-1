package org.rspeer.runetek.providers;

public interface RSTileModel extends RSProvider {
    int getOverlay();
	int getRotation();
	int getShape();
	int getUnderlay();
	boolean isFlatShade();
}