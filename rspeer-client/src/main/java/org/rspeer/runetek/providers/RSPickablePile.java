package org.rspeer.runetek.providers;

public interface RSPickablePile extends RSProvider {

    long getUid();

    int getHeight();

    int getFineX();

    int getFineY();

    RSEntity getBottom();

    RSEntity getMiddle();

    RSEntity getTop();
}