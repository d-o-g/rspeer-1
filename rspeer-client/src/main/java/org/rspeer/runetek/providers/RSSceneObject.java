package org.rspeer.runetek.providers;

import org.rspeer.runetek.providers.annotations.Synthetic;
import org.rspeer.runetek.adapter.scene.SceneObject;

public interface RSSceneObject extends RSProvider {

    default int getFineX() {
        return getSceneX() << 7;
    }

    default int getFineY() {
        return getSceneY() << 7;
    }

    long getUid();

    RSEntity getEntity();

    default RSDynamicObject asDynamicObject() {
        RSEntity entity = getEntity();
        return entity instanceof RSDynamicObject ? (RSDynamicObject) entity : null;
    }

    @Synthetic
    SceneObject getWrapper();

    int getSceneX();

    int getSceneY();

    int getFloorLevel();

    default int getOrientation(){
        return Integer.MIN_VALUE;
    }

    default int getLinkedOrientation(){
        return Integer.MIN_VALUE;
    }

    default int getId() {
        return (int) (getUid() >>> 17 & 0xffffffffL);
    }

    default int getType() {
        return (int) (getUid() >>> 14 & 0x3L);
    }

    default int getConfig() {
        return -1; //only valid for boundarydecor and entitymarker
    }

    default RSEntity getLinkedEntity() {
        return null; //only valid for boundarydecor and boundary
    }
}
