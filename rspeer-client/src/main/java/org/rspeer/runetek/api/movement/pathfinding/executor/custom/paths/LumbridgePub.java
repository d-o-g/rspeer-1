package org.rspeer.runetek.api.movement.pathfinding.executor.custom.paths;

import com.allatori.annotations.DoNotRename;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.executor.custom.CustomPath;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

@DoNotRename
public class LumbridgePub extends CustomPath {

    private final Area LUMBRIDGE_PUB_AREA = Area.polygonal(
            new Position(3234, 3235, 0),
            new Position(3234, 3245, 0),
            new Position(3226, 3245, 0),
            new Position(3226, 3239, 0),
            new Position(3228, 3239, 0),
            new Position(3228, 3235, 0)
    );

    private final Position OUTSIDE_PUB_DOOR = new Position(3230, 3235, 0);

    private final Position LUMBRIDGE_PUB_FIX_POSITION = new Position(3220, 3245, 0).randomize(2);

    private boolean isApplyingFix;

    @DoNotRename
    @Override
    public boolean validate(Positionable start, Positionable end) {
        final int offset = 3;
        if (LUMBRIDGE_PUB_FIX_POSITION.distance() <= offset) {
            isApplyingFix = false;
            return false;
        }
        if(isApplyingFix) {
            return true;
        }
        Position dest = Movement.getDestination();
        if(dest != null && (LUMBRIDGE_PUB_AREA.contains(dest) || dest.equals(OUTSIDE_PUB_DOOR))) {
            isApplyingFix = true;
            return true;
        }
        return false;
    }

    @DoNotRename
    @Override
    public Position getDestination() {
        return LUMBRIDGE_PUB_FIX_POSITION;
    }


    @DoNotRename
    @Override
    public String getName() {
        return "Lumbridge Pub Fix";
    }

}
