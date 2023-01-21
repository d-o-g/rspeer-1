package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.VarpListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spencer on 31/01/2018.
 */
public final class VarpEvent extends Event {

    private static final List<Varpbit>[] cache;

    static {
        cache = new List[Varps.MAX_VARP];
        for (int i = 0; i < 10000; i++) {
            Varpbit bit = Varps.getBit(i);
            if (bit != null) {
                if (cache[bit.getVarpIndex()] == null) {
                    cache[bit.getVarpIndex()] = new ArrayList<>();
                }
                cache[bit.getVarpIndex()].add(bit);
            }
        }
    }

    private final int index;
    private final int oldValue;
    private final int newValue;
    private final List<Varpbit> bits;

    public VarpEvent(int index, int oldValue, int newValue) {
        super("Static");
        this.index = index;
        this.oldValue = oldValue;
        this.newValue = newValue;
        bits = cache[index];
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof VarpListener) {
            ((VarpListener) listener).notify(this);
        }
    }

    public int getIndex() {
        return index;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getNewValue() {
        return newValue;
    }

    public Varpbit[] getChanges() {
        if (bits == null) {
            return new Varpbit[0];
        }
        List<Varpbit> changes = new ArrayList<>();
        for (Varpbit bit : bits) {
            int old = bit.getValue(oldValue);
            int now = bit.getValue(newValue);
            if (old != now) {
                changes.add(bit);
            }
        }
        return changes.toArray(new Varpbit[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Varp[").append(index).append("]").append(" ").append(oldValue)
                .append(" -> ").append(newValue);
        for (Varpbit vb : getChanges()) {
            sb.append("\n").append(vb);
        }
        return sb.toString();
    }
}
