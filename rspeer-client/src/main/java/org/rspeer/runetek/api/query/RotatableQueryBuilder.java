package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.scene.Entity;
import org.rspeer.runetek.api.commons.Rotatable;
import org.rspeer.runetek.api.commons.predicate.OrientationPredicate;

public abstract class RotatableQueryBuilder<T extends Entity & Rotatable, Q extends QueryBuilder>
        extends PositionableQueryBuilder<T, Q> {

    private int[] orientations = null;

    public Q orientation(int... orientations) {
        this.orientations = orientations;
        return self();
    }

    /*public Q facing(Entity... entities) {

    }*/ //TODO

    @Override
    public boolean test(T entity) {
        if (orientations != null && !new OrientationPredicate<>(orientations).test(entity)) {
            return false;
        }

        return super.test(entity);
    }
}
