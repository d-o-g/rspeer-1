package org.rspeer.runetek.api.movement.path;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;

/**
 * A Path, consisting of a set of predefined positions.
 */
public final class PredefinedPath implements Path {

    private final Position[] positions;
    private boolean acceptBlockedEnd = true;
    private Position current;
    private boolean buildSubPaths = true;

    private PredefinedPath(Position... positions) {
        this.positions = positions;
    }

    /**
     * @param positions The positions that the path consists of. Note that
     *                  each position must be within 30 tiles of the next
     * @return A PredefinedPath
     */
    public static PredefinedPath build(Position... positions) {
        return new PredefinedPath(positions);
    }

    public PredefinedPath withBuildSubPaths(boolean build) {
        this.buildSubPaths = build;
        return this;
    }

    public boolean walk(boolean acceptBlockedEnd) {
        this.acceptBlockedEnd = acceptBlockedEnd;
        return walk(PathExecutor.getPathExecutorSupplier().get());
    }

    @Override
    public boolean walk(PathExecutor executor) {
        int nearIndex = -1;
        int lastDistance = Integer.MAX_VALUE;
        for (int i = 0; i < positions.length; i++) {
            Position position = positions[i];
            double dist = position.distance();
            if (dist < lastDistance) {
                lastDistance = (int) dist;
                nearIndex = i;
            }
        }

        Position furthest = null;
        if (nearIndex != positions.length - 1) {
            furthest = positions[nearIndex + 1];
            for (int i = nearIndex; i < positions.length; i++) {
                Position position = positions[i];
                if (Scene.isLoaded(position) && Movement.isWalkable(position, acceptBlockedEnd)) {
                    if (position.distance() <= 5) {
                        continue;
                    }
                    furthest = position;
                }
            }
        }

        current = furthest;

        if (furthest == null) {
            return false;
        }

        if (buildSubPaths) {
            TilePath tilePath = executor.buildSubPath(this, furthest, positions[positions.length - 1]);
            return tilePath != null && executor.execute(tilePath);
        } else {
            return executor.setWalkFlag(this, furthest);
        }
    }

    public Position getCurrent() {
        return current;
    }
}
