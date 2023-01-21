package org.rspeer.runetek.event.snapshot;

public final class UnrealMouseSnapshotProvider implements MouseSnapshotProvider {

    @Override
    public MouseActionRecord interceptActionRecord(MouseActionRecord original) {
        return new MouseActionRecord(original.getPreviousActionTime(), original.getTime(), 0, 0);
    }

    @Override
    public MouseMotionRecord interceptMotionRecord(MouseMotionRecord original) {
        return new MouseMotionRecord(original.getIndex(), original.getTime(), 0, 0);
    }
}
