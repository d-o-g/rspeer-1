package org.rspeer.runetek.providers;

import org.rspeer.runetek.providers.annotations.Synthetic;
import org.rspeer.runetek.api.Varps;

public interface RSVarpbit extends RSDoublyNode {

    int getLower();

    int getUpper();

    int getVarpIndex();

    @Synthetic
    default int getBitCount() {
        return getUpper() - getLower();
    }

    @Synthetic
    default int getMask() {
        return Varps.BIT_MASKS[getBitCount()];
    }

    @Synthetic //partially synthetic
    default int getValue() {
        int varpValue = Varps.get(getVarpIndex());
        return getValue(varpValue);
    }

    @Synthetic
    default int getValue(int varpValue) {
        int mask = Varps.BIT_MASKS[getUpper() - getLower()];
        return varpValue >> getLower() & mask;
    }

    @Synthetic
    default boolean getBoolean() {
        return isBoolean() && getValue() == 1;
    }

    @Synthetic
    default boolean isBoolean() {
        return getBitCount() == 1;
    }
}