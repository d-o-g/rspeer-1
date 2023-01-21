package org.rspeer.runetek.api.commons.math;

import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.adapter.Positionable;

/**
 * An interface for the evaluation of distances
 */
public interface DistanceEvaluator {

    double evaluate(int x1, int y1, int x2, int y2);

    default double evaluate(Positionable from, Positionable to) {
        Position w1 = from.getPosition();
        Position w2 = to.getPosition();
        return evaluate(w1.getX(), w1.getY(), w2.getX(), w2.getY());
    }
}