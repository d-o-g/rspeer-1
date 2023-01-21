package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import com.allatori.annotations.DoNotRename;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.position.Position;

@DoNotRename
public abstract class CustomPath {

    @DoNotRename
    public abstract boolean validate(Positionable start, Positionable end);

    @DoNotRename
    public abstract Position getDestination();

    @DoNotRename
    public abstract String getName();
}

