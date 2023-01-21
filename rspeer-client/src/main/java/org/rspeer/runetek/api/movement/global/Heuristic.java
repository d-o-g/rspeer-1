package org.rspeer.runetek.api.movement.global;

import org.rspeer.runetek.api.commons.math.Distance;

public interface Heuristic {

    Heuristic CHEBYSHEV = (src, dst) -> (float) src.distance(dst, Distance.CHEBYSHEV);

    float getCost(PathNode src, PathNode dst);
}
