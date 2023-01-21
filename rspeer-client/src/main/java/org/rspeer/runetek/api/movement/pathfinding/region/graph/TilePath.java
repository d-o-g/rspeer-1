package org.rspeer.runetek.api.movement.pathfinding.region.graph;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.graph.Edge;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Reachable;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.ui.Log;

import java.util.List;

/**
 * Created by Zachary Herridge on 7/9/2018.
 */
public class TilePath implements Path {

    private List<Edge> path;

    public TilePath(List<Edge> path) {
        this.path = path;
    }

    @Override
    public boolean walk(PathExecutor executor) {
        return walk(executor, Movement.getReachableMap());
    }

    public boolean walk(PathExecutor executor, Reachable reachable) {
        log("Starting exploitation on " + path.size() + " tiles.");

        Edge furthest = null;
        int furthestIndex = -1;
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i) instanceof TileEdge) {
                TileEdge tileEdge = (TileEdge) path.get(i);
                if (tileEdge.getType() == EdgeType.DOOR) {
                    furthest = tileEdge;
                    log("Door found stopping exploitation.");
                    break;
                } else {
                    Position position = tileEdge.getTileEnd().getPosition();
                    boolean result = reachable.isReachable(position, false) && !reachable.isActionRequired(position);
                    double distance = position.distance();
                    log("Checked " + position + " distance " + distance + " " + result);
                    if (result) {
                        furthest = tileEdge;
                        furthestIndex = i;

                        if (distance > 16) {
                            log("Distance limit applied.");
                            break;
                        }
                    }
                }
            }
        }

        log("Exploration complete with " + furthest + " as end node at index " + furthestIndex + ".");
        if (furthest != null) {
            if (furthest.getType() == EdgeType.DOOR) {
                return executor.handleDoor(this, (TileEdge) furthest);
            } else {
                boolean setResult = false;
                do {
                    Position endLocation = furthest.getEnd().getPosition();
                    if (Players.getLocal().getPosition().equals(endLocation)) {
                        log("Already at end location.");
                        break;
                    } else {
                        setResult = executor.setWalkFlag(this, endLocation);
                        log("Attempted to set walk flag to " + endLocation + " with distance " + endLocation.distance() + " and scene flag " + Scene.getCollisionFlag(endLocation) + " result " + setResult + ".");
                        if (!setResult) {
                            int index = path.indexOf(furthest) - 1;
                            log("Setting walk flag failed, rolling back furthest to index " + index + ".");
                            if (index < 0) {
                                break;
                            }
                            furthest = path.get(index);
                            TileNode start = (TileNode) furthest.getStart();
                            if (start == null) {
                                break;
                            }
                        }
                    }
                } while (!setResult);
                return setResult;
            }
        }

        return false;
    }

    private void log(String log) {
        if (Movement.getDebug().isToggled()) {
            Log.info("TilePath - " + log);
        }
    }

    public List<Edge> getPath() {
        return path;
    }

    public TilePath setPath(List<Edge> path) {
        this.path = path;
        return this;
    }
}
