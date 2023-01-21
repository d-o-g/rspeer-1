package org.rspeer.runetek.event.snapshot;

public interface MouseSnapshotProvider {

    MouseActionRecord interceptActionRecord(MouseActionRecord original);

    MouseMotionRecord interceptMotionRecord(MouseMotionRecord original);
}
