package org.rspeer.runetek.api.movement.path;

import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;

/**
 * Created by Zachary Herridge on 2/13/2018.
 */
public interface Path {

    default boolean walk() {
        return walk(PathExecutor.getPathExecutorSupplier().get());
    }

    boolean walk(PathExecutor pathExecutor);
}
