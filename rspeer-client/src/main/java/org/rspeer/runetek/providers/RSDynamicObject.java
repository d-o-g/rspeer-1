package org.rspeer.runetek.providers;

public interface RSDynamicObject extends RSEntity {

    void setId(int id);

    int getId();

    int getFloorLevel();

    int getOrientation();

    int getSceneX();

    int getSceneY();

    int getType();

    RSAnimationSequence getSequence();
}