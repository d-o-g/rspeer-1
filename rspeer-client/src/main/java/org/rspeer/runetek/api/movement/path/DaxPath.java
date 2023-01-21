package org.rspeer.runetek.api.movement.path;

import com.allatori.annotations.DoNotRename;
import org.rspeer.networking.dax.walker.DaxWalker;
import org.rspeer.networking.dax.walker.engine.definitions.WalkCondition;
import org.rspeer.networking.dax.walker.models.WalkState;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.position.Position;

public class DaxPath implements Path {

    private DaxWalker walker;
    private Position position;
    private WalkCondition condition;
    private WalkState state;

    public DaxPath(DaxWalker walker, Position position) {
        this.walker = walker;
        this.position = position;
    }

    public DaxPath(DaxWalker walker, Position position, WalkCondition condition) {
        this.walker = walker;
        this.position = position;
        this.condition = condition;
    }

    @Override
    public boolean walk(PathExecutor pathExecutor) {
        state = condition != null ? walker.walkTo(position, condition)
                : walker.walkTo(position);
        return state == WalkState.SUCCESS;
    }

    @Override
    public boolean walk() {
        state = condition != null ? walker.walkTo(position, condition)
                : walker.walkTo(position);
        return state == WalkState.SUCCESS;
    }

    @DoNotRename
    public DaxWalker getWalker() {
        return walker;
    }

    @DoNotRename
    public WalkState getState() {
        return state;
    }
}