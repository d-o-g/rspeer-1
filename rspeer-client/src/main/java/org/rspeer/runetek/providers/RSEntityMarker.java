package org.rspeer.runetek.providers;

public interface RSEntityMarker extends RSSceneObject {

    int getConfig();

    int getCenterFineX();

    int getCenterFineY();

    int getMaxSceneX();

    int getMaxSceneY();

    int getHeight();

    int getFloorLevel();

    int getOrientation();

    int getSceneX();

    int getSceneY();

    long getUid();

    RSEntity getEntity();
}