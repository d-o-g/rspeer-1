package org.rspeer.runetek.event.snapshot;

import java.io.DataOutput;
import java.io.IOException;

public final class MouseActionRecord extends MouseRecord {

    private final long previousActionTime;

    public MouseActionRecord(long previousActionTime, long time, int x, int y) {
        super(time, x, y);
        this.previousActionTime = previousActionTime;
    }

    public long getPreviousActionTime() {
        return previousActionTime;
    }

    @Override
    public int getType() {
        return TYPE_ACTION;
    }

    @Override
    protected void writeTo(DataOutput output) throws IOException {
        output.writeInt(x | y << 16);
        output.writeLong(time);
        output.writeLong(previousActionTime);
    }
}
