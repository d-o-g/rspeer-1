package org.rspeer.runetek.api.commons.predicate;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.position.Position;

import java.util.function.Predicate;

public class PositionPredicate implements Predicate<Positionable> {

    private final Position[] positions;

    public PositionPredicate(Position... positions) {
        this.positions = positions;
    }

    @Override
    public boolean test(Positionable positionable) {
        for (Position position : positions) {
            if (positionable.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }
}
