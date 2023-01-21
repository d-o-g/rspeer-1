package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;

public final class WalkAction extends Action {

    private final Position position;

    public WalkAction(Position position) {
        super(ActionOpcodes.WALK_HERE, 0, position.getX(), position.getY());
        this.position = position;
    }

    public WalkAction(int sceneX, int sceneY, int floorLevel) {
        this(new ScenePosition(sceneX, sceneY, floorLevel).getPosition());
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("%s[x=%d,y=%d]", super.toString(), position.getX(), position.getY());
    }
}
