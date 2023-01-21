package org.rspeer.runetek.api.movement.pathfinding.region.astar;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.graph.Edge;
import org.rspeer.runetek.api.movement.pathfinding.graph.Node;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileEdge;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileNode;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.ui.Log;

import java.util.*;

/**
 * Created by Zachary Herridge on 7/23/2018.
 */
public class AStar {

    protected int maxAttempts = 11000;
    protected TileNode startNode, destinationNode;

    protected Map<Node, Edge> pathCache = new HashMap<>();
    protected Map<Node, Double> costCache = new HashMap<>();
    protected PriorityQueue<AStarStore> open = new PriorityQueue<>();

    public TilePath buildPath(Positionable destination) {
        return buildPath(Players.getLocal(), destination);
    }

    public TilePath buildPath(Positionable startPositionable, Positionable destinationPositionable) {
        if (startPositionable == null || destinationPositionable == null) {
            return null;
        }


        Position startPosition = Scene.findUnblocked(startPositionable.getPosition());
        Position destinationPosition = destinationPositionable.getPosition();

        if (startPosition == null || destinationPosition == null) {
            return null;
        }

        startNode = new TileNode(startPosition);
        destinationNode = new TileNode(destinationPosition);
        if (startPosition.equals(destinationPosition)) {
            return new TilePath(Collections.singletonList(new TileEdge(startNode, destinationNode)));
        }

        if (!Scene.isLoaded(startPosition) || !Scene.isLoaded(destinationPosition)) {
            log("Path not contained by current Scene.");
            return null;
        }

        if (!Movement.isWalkable(destinationPosition, false)) {
            destinationPosition = Movement.getReachableMap().findNearestAccessiblePosition(destinationPositionable);
        }

        if (destinationPosition == null) {
            return null;
        }

        log("Starting search from " + startPosition + " to " + destinationPosition + ".");

        open.add(new AStarStore(startNode, 0));
        costCache.put(startNode, 0d);

        int attempts = 0;
        while (!open.isEmpty()) {
            attempts++;
            AStarStore current = open.poll();

            if (attempts >= maxAttempts) {
                log("Failed to find path from " + startNode + " to " + destinationNode + " in " + attempts + " attempts.");
                break;
            }

            if (current.getNode().equals(destinationNode)) {
                log("Found path from " + startNode + " to " + destinationNode + " in " + attempts + " attempts.");
                List<Edge> path = collectPath(destinationNode, startNode);
                if (!Movement.getDebug().isToggled()) {
                    clear();
                }
                return new TilePath(path);
            }

            for (TileEdge edge : new TileNode(current.getNode().getPosition()).getNeighbors(true)) {
                evaluate(edge, current);
            }
        }

        if (!Movement.getDebug().isToggled()) {
            clear();
        } else {
            Movement.getDebug().setLastAStar(this);
        }

        return null;
    }

    private void evaluate(Edge edge, AStarStore current) {
        Node next = edge.getEnd();
        double newCost = costCache.getOrDefault(current.getNode(), 0d) + heuristic(edge.getStart(), edge.getEnd(), edge.getCostPenalty());
        Double oldCost = costCache.get(next);
        if (oldCost == null || newCost < oldCost) {
            costCache.put(next, newCost);
            double priority = newCost + heuristic(next, destinationNode, edge.getCostPenalty());
            open.add(new AStarStore(next, priority));
            pathCache.put(next, edge);
        }
    }

    protected double heuristic(Node current, Node end, double costPenalty) {
        return current.getPosition().distance(end.getPosition()) + costPenalty;
    }

    protected List<Edge> collectPath(Node end, Node start) {
        List<Edge> path = new ArrayList<>();
        Edge edge = pathCache.get(end);
        while (edge != null) {
            path.add(edge);
            if (edge.getStart().getPosition().equals(start.getPosition())) {
                break;
            }
            edge = pathCache.get(edge.getStart());
        }
        Collections.reverse(path);
        return path;
    }

    protected void clear() {
        open.clear();
        costCache.clear();
        pathCache.clear();
    }

    protected void log(String log) {
        if (Movement.getDebug().isToggled()) {
            Log.info("AStar - " + log);
        }
    }

    public TileNode getStartNode() {
        return startNode;
    }

    public TileNode getDestinationNode() {
        return destinationNode;
    }

    public Map<Node, Double> getCostCache() {
        return costCache;
    }

    public Map<Node, Edge> getPathCache() {
        return pathCache;
    }

    public PriorityQueue<AStarStore> getOpen() {
        return open;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
