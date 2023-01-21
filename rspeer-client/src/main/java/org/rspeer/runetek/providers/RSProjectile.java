package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.scene.Projectile;

public interface RSProjectile extends RSEntity {
    int getStartX();

    int getStartY();

    double getHeightOffset();

    double getSpeed();

    double getSpeedX();

    double getSpeedY();

    double getSpeedZ();

    int getEndCycle();

    int getHeight();

    int getId();

    int getFloorLevel();

    int getSlope();

    int getStartCycle();

    double getFineX();

    double getFineY();

    int getTargetDistance();

    int getTargetIndex();

    int getXRotation();

    int getYRotation();

    RSAnimationSequence getSequence();

    boolean isInMotion();

    Projectile getWrapper();
}