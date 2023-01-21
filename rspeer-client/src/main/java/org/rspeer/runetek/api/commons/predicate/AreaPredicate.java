package org.rspeer.runetek.api.commons.predicate;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

import java.util.function.Predicate;

public class AreaPredicate implements Predicate<Positionable> {

    private final Area[] areas;

    public AreaPredicate(Area... areas) {
        this.areas = areas;
    }

    @Override
    public boolean test(Positionable positionable) {
        for (Area area : areas) {
            if (area.contains(positionable)) {
                return true;
            }
        }
        return false;
    }
}
