package org.rspeer.runetek.api.commons.predicate;

import org.rspeer.runetek.api.commons.Animable;

import java.util.function.Predicate;

public class AnimationPredicate<I extends Animable> implements Predicate<I> {

    private final int[] ids;

    public AnimationPredicate(int... ids) {
        this.ids = ids;
    }

    @Override
    public boolean test(I i) {
        for (int ids : ids) {
            if (ids == i.getAnimation()) {
                return true;
            }
        }
        return false;
    }
}
