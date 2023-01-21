package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.math.DistanceEvaluator;
import org.rspeer.runetek.api.commons.predicate.AreaPredicate;
import org.rspeer.runetek.api.commons.predicate.PositionPredicate;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;

public abstract class PositionableQueryBuilder<T extends Positionable, Q extends QueryBuilder>
        extends QueryBuilder<T, Q, PositionableQueryResults<T>> {

    private Boolean reachable = null;

    private DistanceEvaluator distanceEvaluator = Distance.getDefaultEvaluator();

    private Integer distanceFromDefined = null;
    private Integer distanceFromLocal = null;

    private Positionable from = null;

    private Area[] areas = null;

    private Position[] positions = null;

    public Q on(Position... positions) {
        this.positions = positions;
        return self();
    }

    public Q reachable() {
        reachable = true;
        return self();
    }

    public Q unreachable() {
        reachable = false;
        return self();
    }

    public Q within(Positionable src, int distance) {
        from = src;
        this.distanceFromDefined = distance;
        return self();
    }

    public Q within(int distance) {
        distanceFromLocal = distance;
        return self();
    }

    public Q within(Area... areas) {
        this.areas = areas;
        return self();
    }

    public Q distanceEvaluator(DistanceEvaluator distanceEvaluator) {
        this.distanceEvaluator = distanceEvaluator;
        return self();
    }

    @Override
    public boolean test(T entity) {
        if (distanceFromLocal != null && entity.distance(distanceEvaluator) > distanceFromLocal) {
            return false;
        }

        if (distanceFromDefined != null && from != null && from.distance(entity, distanceEvaluator) > distanceFromDefined) {
            return false;
        }

        if (positions != null && !new PositionPredicate(positions).test(entity)) {
            return false;
        }

        if (areas != null && !new AreaPredicate(areas).test(entity)) {
            return false;
        }

        if (reachable != null && reachable != Movement.isInteractable(entity)) {
            return false;
        }

        return super.test(entity);
    }
}
