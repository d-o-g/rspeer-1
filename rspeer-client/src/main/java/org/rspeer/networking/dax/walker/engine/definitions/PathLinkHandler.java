package org.rspeer.networking.dax.walker.engine.definitions;

import com.allatori.annotations.DoNotRename;
import org.rspeer.runetek.api.movement.position.Position;

@DoNotRename
public interface PathLinkHandler {
    @DoNotRename
    PathHandleState handle(Position start, Position end, WalkCondition walkCondition);
}
