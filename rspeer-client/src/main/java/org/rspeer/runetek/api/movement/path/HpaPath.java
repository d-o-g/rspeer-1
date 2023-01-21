package org.rspeer.runetek.api.movement.path;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.NodeType;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.cache.AbstractHpaCache;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;
import org.rspeer.runetek.api.movement.pathfinding.region.astar.AStar;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
public class HpaPath implements Path {

    private List<HpaEdge> path;
    private Map<String, List<HpaLocation>> subPaths;
    private AbstractHpaCache cache;
    private Position destination;
    private int lastExecuteIndex = -1;
    private HpaEdge furthestEdge = null;
    private Position furthestPosition = null;
    private TilePath lastTilePath;

    /**
     * Finds a path from the current position to the specified destination position
     *
     * @param destination The destination position
     * @return A HpaPath, or null if no path was found
     */
    public static HpaPath build(Position destination) {
        return build(Players.getLocal().getPosition(), destination);
    }

    /**
     * Finds a path from one position to the other
     *
     * @param source      The source position
     * @param destination The destination position
     * @return A HpaPath, or null if no path was found
     */
    public static HpaPath build(Position source, Position destination) {
        AbstractHpaCache instance = AbstractHpaCache.getInstance();
        return instance == null ? null : instance.getPath(source, destination);
    }

    public HpaEdge getNext(HpaEdge current) {
        int index = path.indexOf(current) + 1;
        return index < path.size() ? path.get(index) : null;
    }

    private Position getFurthestPositionInSubPath(List<HpaLocation> subPath) {
        if (subPath == null) {
            return null;
        }
        Position furthest = null;
        for (HpaLocation hpaLocation : subPath) {
            Position position = hpaLocation.toPosition();
            boolean result = Movement.isWalkable(hpaLocation.toPosition(), false);
            double distance = position.distance();
            if (distance > 16 && furthest != null) {
                //System.out.println("Distance break.");
                return furthest;
            }

            log("Sub Path - Checked " + position + " distance " + position.distance() + " " + result);
            if (result) {
                furthest = position;
            }
        }
        return furthest;
    }

    private int exploreFrom(int startIndex) {
        log("Starting exploration of edge at index " + startIndex + (lastExecuteIndex == -1 ? "." : (", last execution finished at index " + lastExecuteIndex + ".")));

        int lastViable = -1;
        int currentIndex;
        for (currentIndex = startIndex; currentIndex < path.size(); currentIndex++) {
            HpaEdge hpaEdge = path.get(currentIndex);
            if (hpaEdge.getType() == EdgeType.PLANE_CHANGE) {
                if (Movement.isWalkable(hpaEdge.getStart().getPosition(), true)) {
                    furthestEdge = hpaEdge;
                    furthestPosition = null;
                    log("Edge at index " + currentIndex + " is plane change, setting to furthest and stopping exploration.");
                    break;
                }
            } else if (hpaEdge.getType() == EdgeType.CUSTOM) {
                if (lastViable != -1) {
                    boolean viable = Movement.isWalkable(hpaEdge.getStart().getPosition(), true) || Movement.isWalkable(hpaEdge.getEnd().getPosition(), true);
                    if (!viable) {
                        log("Failed to find viable position in custom edge at index " + currentIndex + " with viable edge at index " + lastViable + ", stopping exploration.");
                        break;
                    }
                }

                log("New furthest custom edge at index " + currentIndex + ".");
                furthestEdge = hpaEdge;
                lastViable = currentIndex;
            } else {
                Position furthestPortionInEdge = getFurthestPositionInEdge(hpaEdge);
                if (furthestPortionInEdge != null) {
                    if (furthestEdge != null && furthestEdge.getType() == EdgeType.CUSTOM) {
                        int nextLength = getLengthTo(Players.getLocal().getPosition(), furthestPortionInEdge);
                        double customValue = getLengthTo(Players.getLocal().getPosition(), furthestEdge.getStart().getPosition()) + furthestEdge.getCost() + furthestEdge.getCostPenalty() + getLengthTo(furthestEdge.getEnd().getPosition(), furthestPortionInEdge);

                        log("Checked custom edge vs next node " + customValue + " to " + nextLength + ".");
                        if (customValue < nextLength) {
                            log("Last custom edge preforms better than next node, stopping exploration.");
                            break;
                        }
                    }

                    double distance = furthestPortionInEdge.distance();
                    log("New furthest position from edge index " + currentIndex + " with position distance " + distance + ".");

                    lastViable = currentIndex;
                    furthestEdge = hpaEdge;
                    furthestPosition = furthestPortionInEdge;
                } else if (lastViable != -1) {
                    log("Failed to find viable position in edge index " + currentIndex + " with viable edge at index " + lastViable + ", stopping exploration.");
                    break;
                }
            }
        }

        return lastViable;
    }


    private int getLengthTo(Position start, Position destination) {
        return Optional.ofNullable(new AStar().buildPath(start, destination)).map(TilePath::getPath).map(List::size).orElse(10000);
    }

    private Position getFurthestPositionInEdge(HpaEdge hpaEdge) {
        Position furthestPortionInEdge = null;

        if (Movement.isWalkable(hpaEdge.getEnd().getPosition(), hpaEdge.getEnd().getType() == NodeType.STAIR)) {
            furthestPortionInEdge = hpaEdge.getEnd().getPosition();
        }
        if (furthestPortionInEdge == null) {
            furthestPortionInEdge = getFurthestPositionInSubPath(subPaths.get(String.valueOf(hpaEdge.getPathKey())));
        }
        if (furthestPortionInEdge == null && Movement.isWalkable(hpaEdge.getStart().getPosition(), hpaEdge.getStart().getType() == NodeType.STAIR)) {
            furthestPortionInEdge = hpaEdge.getStart().getPosition();
        }

        if (Players.getLocal().getPosition().equals(furthestPortionInEdge)) {
            furthestPortionInEdge = null;
        }

        return furthestPortionInEdge;
    }

    @Override
    public boolean walk(PathExecutor executor) {
        int startIndex = Math.max(0, lastExecuteIndex - 1);
        int currentIndex = exploreFrom(startIndex);

        if (startIndex != 0 && furthestEdge == null) {
            log("Failed to find viable edge while starting at non-zero index, trying again from zero.");
            currentIndex = exploreFrom(0);
        }

        log("Exploration complete at index " + currentIndex + " with " + furthestEdge + " as end edge and " + furthestPosition + " as position.");
        boolean result = false;
        if (furthestEdge != null) {
            lastExecuteIndex = path.lastIndexOf(furthestEdge);

            if (furthestEdge.getType() == EdgeType.CUSTOM) {
                result = executor.handleCustomEdge(this, furthestEdge);
            } else if (furthestEdge.getType() == EdgeType.PLANE_CHANGE) {
                boolean positiveLevelChange = furthestEdge.getStart().getPosition().getFloorLevel() - furthestEdge.getEnd().getPosition().getFloorLevel() < 0;
                result = executor.handlePlaneChange(this, furthestEdge.getStart().getPosition(), positiveLevelChange);
            } else if (furthestPosition != null) {
                TilePath localPath = executor.buildSubPath(this, furthestPosition, destination);
                if (Movement.getDebug().isToggled()) {
                    lastTilePath = localPath;
                }
                result = localPath != null && executor.execute(localPath);
            }
        }

        if (result) {
            refreshCache();
        } else {
            decache();
        }

        return result;
    }

    public void refreshCache() {
        if (cache != null) {
            cache.refresh(this);
        }
    }

    public void decache() {
        if (cache != null) {
            cache.decache(this);
        }
    }

    public Map<String, List<HpaLocation>> getSubPaths() {
        return subPaths;
    }

    public void setSubPaths(Map<String, List<HpaLocation>> subPaths) {
        this.subPaths = subPaths;
    }

    private void log(String log) {
        if (Movement.getDebug().isToggled()) {
            Log.info("HpaPath - " + log);
        }
    }

    public List<HpaEdge> getPath() {
        return path;
    }

    public void setPath(List<HpaEdge> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "HpaPath{" +
                "path=" + path +
                '}';
    }

    public void setCache(AbstractHpaCache cache) {
        this.cache = cache;
    }

    public TilePath getLastTilePath() {
        return lastTilePath;
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }
}
