package org.rspeer.runetek.providers;

public interface RSCollisionMap extends RSProvider {
    int getHeight();
	int getInsetX();
	int getInsetY();
	int getWidth();
	int[][] getFlags();
}