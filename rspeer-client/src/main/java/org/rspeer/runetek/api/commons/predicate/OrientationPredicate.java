package org.rspeer.runetek.api.commons.predicate;

import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.Rotatable;

import java.util.function.Predicate;

public class OrientationPredicate<I extends Rotatable> implements Predicate<I> {

    private final int[] orientations;

    public OrientationPredicate(int... orientations) {
        this.orientations = orientations;
    }

    @Override
    public boolean test(I i) {
        for (int orientation : orientations) {
            if (orientation == i.getOrientation()) {
                return true;
            }
        }
        return false;
    }
}