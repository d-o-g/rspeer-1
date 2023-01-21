package org.rspeer.ui.debug.varpexlorer;

import org.rspeer.runetek.adapter.Varpbit;

/**
 * @author Yasper
 * <p>
 * Encapsulates a change in a varpbit.
 */
public final class VarpbitChange {

    private final Varpbit bit;
    private final int previous;
    private final int current;

    public VarpbitChange(Varpbit bit, int previous, int current) {
        this.bit = bit;
        this.previous = previous;
        this.current = current;
    }

    public Varpbit getBit() {
        return bit;
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }
}
