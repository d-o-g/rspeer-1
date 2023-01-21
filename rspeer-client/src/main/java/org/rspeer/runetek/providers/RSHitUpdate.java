package org.rspeer.runetek.providers;

public interface RSHitUpdate extends RSNode {
    int getCurrentCycle();
	int getCurrentWidth();
	int getStartCycle();
	int getStartWidth();
}