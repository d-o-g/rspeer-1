package org.rspeer.runetek.event.snapshot;

import java.io.DataOutput;
import java.io.IOException;

public final class MouseMotionRecord extends MouseRecord {

    private final int index;

    public MouseMotionRecord(int index, long time, int x, int y) {
        super(time, x, y);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int getType() {
        return TYPE_MOTION;
    }

    @Override
    protected void writeTo(DataOutput output) throws IOException {
        output.writeInt(x | y << 16);
        output.writeLong(time);
        output.writeInt(index);
    }
}
