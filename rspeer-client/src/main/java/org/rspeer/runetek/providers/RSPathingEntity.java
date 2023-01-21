package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.providers.annotations.Synthetic;

public interface RSPathingEntity extends RSEntity {
    void addHitSplat(int type, int amount, int id, int special, int startCycle, int endCycle);

    void addHitUpdate(int id, int startCycle, int currentWidth, int duration, int startWidth, int currentCycle);

    byte getHitsplatCount();

    int getAnimation();

    int getAnimationDelay();

    int getAnimationFrame();

    int getGraphic();

    int getWalkingStance();

    int getOrientation();

    int getPathQueueSize();

    int getStance();

    int getFineX();

    int getFineY();

    int getStanceFrame();

    int getTargetIndex();

    RSLinkedList getHealthBars();

    byte[] getPathQueueTraversed();

    int[] getHitsplatCycles();

    int[] getHitsplatIds();

    int[] getHitsplatTypes();

    int[] getHitsplats();

    int[] getPathXQueue();

    int[] getPathYQueue();

    int[] getSpecialHitsplats();

    PathingEntity getWrapper();

    @Synthetic
    int getIndex();

    String getOverheadText();

    int getAnimationFrameCycle();
}