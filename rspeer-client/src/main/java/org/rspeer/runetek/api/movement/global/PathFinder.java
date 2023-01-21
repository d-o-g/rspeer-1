package org.rspeer.runetek.api.movement.global;

import java.util.List;

public interface PathFinder {
    List<PathNode> findPath(Heuristic heuristic, PathNode src, PathNode dst);
}
