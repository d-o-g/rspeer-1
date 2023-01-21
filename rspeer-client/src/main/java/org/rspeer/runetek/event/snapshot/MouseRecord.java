package org.rspeer.runetek.event.snapshot;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class MouseRecord {

    public static final byte TYPE_MOTION = 0;
    public static final byte TYPE_ACTION = 1;

    protected final long time;
    protected final int x;
    protected final int y;

    protected MouseRecord(long time, int x, int y) {
        this.time = time;
        this.x = x;
        this.y = y;
    }

    public static MouseRecord decode(DataInput input) throws IOException {
        MouseRecord record;
        int type = input.readByte();

        int point = input.readInt();
        int x = point & 0xffff;
        int y = point >> 16;
        long time = input.readLong();

        if (type == TYPE_MOTION) {
            record = new MouseMotionRecord(input.readInt(), time, x, y);
        } else if (type == TYPE_ACTION) {
            record = new MouseActionRecord(input.readLong(), time, x, y);
        } else {
            throw new IllegalStateException("Incompatible type");
        }

        return record;
    }

    public abstract int getType();

    public final long getTime() {
        return time;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final void encode(DataOutput output) throws IOException {
        output.writeByte(getType());
        writeTo(output);
    }

    protected abstract void writeTo(DataOutput output) throws IOException;
}
