package org.rspeer.runetek.api.commons.predicate;

import org.rspeer.runetek.adapter.scene.PathingEntity;

import java.util.function.Predicate;

public class StancePredicate<I extends PathingEntity> implements Predicate<I> {

    private final int[] ids;

    public StancePredicate(int... ids) {
        this.ids = ids;
    }

    @Override
    public boolean test(I i) {
        for (int ids : ids) {
            if (ids == i.getStance()) {
                return true;
            }
        }
        return false;
    }
}
