package org.rspeer.runetek.api.movement.path;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spencer on 21/05/2018.
 */
public final class BresenhamPath implements Path {

    private static int maxAttempts = 5000;

    private List<Position> line;
    private TilePath lastTilePath;
    private Position destinationPosition;

    public BresenhamPath(List<Position> line, Position destinationPosition) {
        this.line = line;
        this.destinationPosition = destinationPosition;
    }

    public static BresenhamPath build(Positionable destination) {
        return build(Players.getLocal().getPosition(), destination);
    }

    public static BresenhamPath build(Positionable start, Positionable destination) {
        Position startPosition = start.getPosition();
        Position destinationPosition = destination.getPosition();

        int startX = startPosition.getX();
        int startY = startPosition.getY();
        int destinationX = destination.getX();
        int destinationY = destinationPosition.getY();

        int deltaX = Math.abs(destinationX - startX);
        int deltaY = Math.abs(destinationY - startY);

        int sx = startX < destinationX ? 1 : -1;
        int sy = startY < destinationY ? 1 : -1;

        int err = deltaX - deltaY;
        int e2;

        List<Position> line = new ArrayList<>();
        int attempts = 0;
        while (attempts < maxAttempts) {
            attempts++;

            line.add(new Position(startX, startY));

            if (startX == destinationX && startY == destinationY) {
                break;
            }

            e2 = 2 * err;
            if (e2 > -deltaY) {
                err = err - deltaY;
                startX = startX + sx;
            }

            if (e2 < deltaX) {
                err = err + deltaX;
                startY = startY + sy;
            }
        }

        return new BresenhamPath(line, destinationPosition);
    }

    public static int getMaxAttempts() {
        return maxAttempts;
    }

    public static void setMaxAttempts(int maxAttempts) {
        BresenhamPath.maxAttempts = maxAttempts;
    }

    public TilePath getLastTilePath() {
        return lastTilePath;
    }

    @Override
    public boolean walk(PathExecutor executor) {
        log("Starting exploitation on " + line.size() + " tiles.");

        Position furthest = null;
        for (Position position : line) {
            boolean result = Movement.isWalkable(position, false);
            log("Checked " + position + " distance " + position.distance() + " " + result);
            if (result) {
                furthest = position;
            }
        }

        log("Exploration complete with " + furthest + " as end node.");
        if (furthest == null) {
            return false;
        }

        TilePath tilePath = executor.buildSubPath(this, furthest, destinationPosition);
        if (Movement.getDebug().isToggled()) {
            lastTilePath = tilePath;
        }
        return tilePath != null && executor.execute(tilePath);
    }

    public List<Position> getLine() {
        return line;
    }

    private void log(String log) {
        if (Movement.getDebug().isToggled()) {
            Log.info("BresenhamPath - " + log);
        }
    }
}
