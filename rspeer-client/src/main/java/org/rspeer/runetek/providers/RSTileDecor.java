package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.scene.Projection;

public interface RSTileDecor extends RSSceneObject {
    int getFloorLevel();

    int getConfig();

    int getFineX();

    int getFineY();

    default int getSceneX() {
        return getFineX() / Projection.TILE_PIXEL_SIZE;
    }

    default int getSceneY() {
        return getFineY() / Projection.TILE_PIXEL_SIZE;
    }

    long getUid();

    RSEntity getEntity();
}