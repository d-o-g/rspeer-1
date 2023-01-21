package org.rspeer.runetek.providers;

public interface RSCameraCapture extends RSProvider {
    int getComponentHeight();
	int getFlag();
	int getMaxStrictX();
	int getMaxStrictY();
	int getMinStrictX();
	int getMinStrictY();
	int getTileHeight();
}