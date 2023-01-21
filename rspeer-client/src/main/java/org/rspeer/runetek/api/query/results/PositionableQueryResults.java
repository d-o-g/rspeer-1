package org.rspeer.runetek.api.query.results;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.math.DistanceEvaluator;
import org.rspeer.runetek.api.scene.Players;

import java.util.Collection;
import java.util.Comparator;

public final class PositionableQueryResults<T extends Positionable> extends QueryResults<T, PositionableQueryResults<T>> {

    public PositionableQueryResults(Collection<? extends T> results) {
        super(results);
    }

    public final PositionableQueryResults<T> sortByDistanceFrom(Positionable src, DistanceEvaluator eval) {
        return sort(Comparator.comparingDouble(value -> eval.evaluate(src, value)));
    }

    public final PositionableQueryResults<T> sortByDistanceFrom(Positionable src) {
        return sortByDistanceFrom(src, Distance.EUCLIDEAN_SQUARED);
    }

    public final PositionableQueryResults<T> sortByDistance(DistanceEvaluator eval) {
        return sortByDistanceFrom(Players.getLocal(), eval);
    }

    public final PositionableQueryResults<T> sortByDistance() {
        return sortByDistanceFrom(Players.getLocal());
    }

    public final T nearest() {
        return sortByDistance().first();
    }

    public final T furthest() {
        return sortByDistance().last();
    }

    public final T nearest(DistanceEvaluator evaluator) {
        return sortByDistance(evaluator).first();
    }

    public final T furthest(DistanceEvaluator evaluator) {
        return sortByDistance(evaluator).last();
    }

    public final T nearestTo(Positionable positionable) {
        return sortByDistanceFrom(positionable).first();
    }

    public final T furthestFrom(Positionable positionable) {
        return sortByDistanceFrom(positionable).last();
    }

    public final T nearestTo(Positionable positionable, DistanceEvaluator evaluator) {
        return sortByDistanceFrom(positionable, evaluator).first();
    }

    public final T furthestFrom(Positionable positionable, DistanceEvaluator evaluator) {
        return sortByDistanceFrom(positionable, evaluator).last();
    }
}