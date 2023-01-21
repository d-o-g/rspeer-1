package org.rspeer.runetek.providers;

public interface RSGraphicsObject extends RSEntity {
    int getHeight();

    int getId();

    int getFloorLevel();

    int getFineX();

    int getFineY();

    RSAnimationSequence getSequence();

    boolean isFinished();
}