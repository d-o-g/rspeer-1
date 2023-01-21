package org.rspeer.runetek.api.movement.pathfinding.region.util;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.graph.Edge;
import org.rspeer.runetek.api.movement.pathfinding.hpa.HpaGenerationData;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileEdge;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileNode;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSObjectDefinition;

import java.util.*;
import java.util.stream.Collectors;

public class Reachable {

    private long buildTime;
    private Position start;

    private Map<Position, TileEdge> cameFrom = new HashMap<>();
    private Set<Position> requiresAction = new HashSet<>();

    public static SceneObject findDoor(Positionable positionable) {
        return findDoor(null, positionable);
    }

    public static SceneObject findDoor(Direction direction, Positionable positionable) {
        if (positionable == null) {
            return null;
        }
        Position position = positionable.getPosition();
        if (position == null) {
            return null;
        }
        SceneObject[] sceneObjects = SceneObjects.getAt(position);
        if (sceneObjects == null) {
            return null;
        }

        for (SceneObject sceneObject : sceneObjects) {
            if (sceneObject == null) {
                continue;
            }

            if (direction != null && sceneObject.getOrientation() != 0 && !direction.isSameAxis(sceneObject.getOrientation())) {
                continue;
            }

            RSObjectDefinition definition = sceneObject.getDefinition();
            if (definition == null) {
                continue;
            }

            if (HpaGenerationData.isDoor(positionable.getPosition(), sceneObject.getName(), sceneObject.getActions(), definition.getMapDoorFlag())) {
                return sceneObject;
            }
        }

        return null;
    }

    public static boolean containsDoor(Direction direction, Position startLocation) {
        return findDoor(direction, startLocation) != null;
    }

    public Reachable build(Positionable startPositionable) {
        buildTime = System.currentTimeMillis();
        clear();

        start = startPositionable.getPosition();
        TileNode startNode = new TileNode(start);

        Set<TileNode> closed = new HashSet<>();
        closed.add(startNode);

        Queue<TileNode> basicQueue = new LinkedList<>();
        Queue<TileNode> nonBasicQueue = new LinkedList<>();

        basicEvaluate(startNode, closed, basicQueue, nonBasicQueue, true);

        while (!basicQueue.isEmpty()) {
            basicEvaluate(basicQueue.poll(), closed, basicQueue, nonBasicQueue, false);
        }

        while (!nonBasicQueue.isEmpty()) {
            nonBasicEvaluate(nonBasicQueue.poll(), closed, nonBasicQueue);
        }

        return this;
    }

    private void basicEvaluate(TileNode poll, Set<TileNode> closed, Queue<TileNode> basicQueue, Queue<TileNode> nonBasicQueue, boolean ignoreBlocked) {
        for (TileEdge edge : poll.getNeighbors(ignoreBlocked)) {
            TileNode endNode = edge.getTileEnd();
            Position endPosition = edge.getTileEnd().getPosition();

            boolean basicEdge = edge.getType() == EdgeType.BASIC;

            if (!endPosition.equals(start)) {
                if (basicEdge) {
                    requiresAction.remove(endPosition);
                } else {
                    requiresAction.add(endPosition);
                }
                cameFrom.putIfAbsent(endPosition, edge);
            }

            if (!closed.contains(endNode)) {
                if (basicEdge) {
                    basicQueue.add(endNode);
                } else {
                    nonBasicQueue.add(endNode);
                }
                closed.add(endNode);
            }
        }
    }

    private void nonBasicEvaluate(TileNode poll, Set<TileNode> closed, Queue<TileNode> nonBasicQueue) {
        for (TileEdge edge : poll.getNeighbors()) {
            TileNode endNode = edge.getTileEnd();
            Position endPosition = edge.getTileEnd().getPosition();

            if (!endPosition.equals(start)) {
                boolean alreadyConnected = cameFrom.containsKey(endPosition);
                if (!alreadyConnected) {
                    requiresAction.add(endPosition);
                }
                cameFrom.putIfAbsent(endPosition, edge);
            }

            if (!closed.contains(endNode)) {
                nonBasicQueue.add(endNode);
                closed.add(endNode);
            }
        }
    }

    public boolean isActionRequired(Positionable positionable) {
        return requiresAction.contains(positionable.getPosition());
    }

    public Position findNearestAccessiblePosition(Positionable positionable) {
        boolean seen = false;
        Position best = null;
        Comparator<Position> comparator = Comparator.comparingDouble(o -> start.distance(o));
        for (Position position : findAccessiblePositions(positionable)) {
            if (!seen || comparator.compare(position, best) < 0) {
                seen = true;
                best = position;
            }
        }
        return seen ? best : null;
    }

    public Set<Position> findAccessiblePositions(Positionable positionable) {
        if (positionable instanceof SceneObject) {
            return findAccessiblePositions(positionable.getPosition(), (SceneObject) positionable);
        }
        return findAccessiblePositions(positionable.getPosition(), SceneObjects.getAt(positionable.getPosition()));
    }

    public Set<Position> findAccessiblePositions(Position position, SceneObject... sceneObjects) {
        if (sceneObjects == null) {
            return Collections.emptySet();
        }

        Set<Position> accessiblePositions = new HashSet<>();

        if (isReachable(position, false)) {
            accessiblePositions.add(position);
            for (Direction direction : Direction.values()) {
                if (!direction.isCardinal()) {
                    continue;
                }
                Position neighbor = position.translate(direction.getXOff(), direction.getYOff());
                if (isReachable(neighbor, false)) {
                    accessiblePositions.add(neighbor);
                }
            }
            return accessiblePositions;
        } else {
            Queue<Position> open = new LinkedList<>();
            Set<Position> closed = new HashSet<>();
            open.add(position);

            Set<String> names = Arrays.stream(sceneObjects)
                    .filter(Objects::nonNull).filter(x -> x.getType() == 2)
                    .map(SceneObject::getName).filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            int attempts = 0;
            while (!open.isEmpty()) {
                attempts++;
                if (attempts > 100 || accessiblePositions.size() >= 10) {
                    break;
                }

                Position poll = open.poll();
                closed.add(poll);

                for (TileEdge potentialEdge : new TileNode(poll).getNeighbors(true)) {
                    Position endLocation = potentialEdge.getTileEnd().getPosition();
                    if (isReachable(endLocation, false) && !isActionRequired(endLocation)) {
                        accessiblePositions.add(endLocation);
                    }
                }

                if (!isReachable(poll, false)) {
                    continue;
                }

                for (Direction direction : Direction.values()) {
                    Position translate = poll.translate(direction.getXOff(), direction.getYOff());
                    if (names.size() > 0 && !closed.contains(translate)) {
                        boolean containsObject = Predicates.matching(x -> names.contains(String.valueOf(x.getName())), SceneObjects.getAt(translate));
                        if (containsObject) {
                            open.add(translate);
                        }
                    }
                }
            }

            return accessiblePositions;
        }
    }

    public TilePath buildPath(Positionable end) {
        if (end == null || end.getFloorLevel() != start.getFloorLevel()) {
            return null;
        }

        List<Edge> path = new ArrayList<>();

        if (start.equals(end.getPosition())) {
            path.add(new TileEdge(new TileNode(start), new TileNode(end.getPosition())));
        } else {
            Position position = end.getPosition();
            TileEdge current = cameFrom.get(position);
            if (current == null) {
                position = findNearestAccessiblePosition(end);
                if (position != null) {
                    current = cameFrom.get(position);
                }
            }

            if (current == null) {
                return null;
            }

            while (current != null) {
                path.add(current);
                current = cameFrom.get(current.getTileStart().getPosition());
            }
            Collections.reverse(path);
        }

        return new TilePath(path);
    }

    public boolean isReachable(Positionable positionable, boolean applyObjectCheck) {
        if (positionable.getFloorLevel() != start.getFloorLevel()) {
            return false;
        }

        if (positionable.getPosition().equals(start.getPosition())) {
            return true;
        }

        if (cameFrom.containsKey(positionable.getPosition())) {
            return true;
        }

        return applyObjectCheck && !findAccessiblePositions(positionable).isEmpty();
    }

    public Map<Position, TileEdge> getCameFrom() {
        return cameFrom;
    }

    public Position getStart() {
        return start;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public void clear() {
        cameFrom.clear();
        requiresAction.clear();
    }
}
