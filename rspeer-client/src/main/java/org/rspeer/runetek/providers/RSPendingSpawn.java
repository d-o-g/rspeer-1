package org.rspeer.runetek.providers;

public interface RSPendingSpawn extends RSNode {
    int getDelay();
	int getHitpoints();
	int getId();
	int getFloorLevel();
	int getOrientation();
	int getSceneX();
	int getSceneY();
	int getType();
}