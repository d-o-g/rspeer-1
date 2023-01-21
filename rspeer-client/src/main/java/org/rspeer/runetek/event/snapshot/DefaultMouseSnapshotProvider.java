package org.rspeer.runetek.event.snapshot;

public final class DefaultMouseSnapshotProvider implements MouseSnapshotProvider {

    @Override
    public MouseActionRecord interceptActionRecord(MouseActionRecord original) {
        return original;
    }

    @Override
    public MouseMotionRecord interceptMotionRecord(MouseMotionRecord original) {
        return original;
    }
}
