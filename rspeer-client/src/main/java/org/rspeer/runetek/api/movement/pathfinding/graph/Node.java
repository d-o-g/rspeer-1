package org.rspeer.runetek.api.movement.pathfinding.graph;

import org.rspeer.runetek.adapter.Positionable;

/**
 * Created by Zachary Herridge on 8/1/2018.
 */
public interface Node extends Positionable {
    int getType();
}
