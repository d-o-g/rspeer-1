package org.rspeer.runetek.api.movement.pathfinding.executor;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.executor.custom.*;
import org.rspeer.runetek.api.movement.pathfinding.hpa.HpaGenerationData;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeData;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.pathfinding.region.astar.AStar;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileEdge;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileNode;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Reachable;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;

import java.util.*;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 7/24/2018.
 */
@Deprecated
public class PathExecutor {


    private static Supplier<PathExecutor> pathExecutorSupplier = PathExecutor::new;
    protected boolean randomizeAll = false;
    private HashMap<Integer, CustomEdgeExecutor> executors = new HashMap<Integer, CustomEdgeExecutor>() {{
        put(EdgeInteraction.FAIRY_RING, new FairyRingExecutor());
        put(EdgeInteraction.SPELL, new TeleportExecutor());
        put(EdgeInteraction.CHARTER, new CharterShipExecutor());
        put(EdgeInteraction.SCENE_ENTITY, new CustomObjectExecutor());
        put(EdgeInteraction.INTERFACE, new InterfaceExecutor());
    }};

    public static Supplier<PathExecutor> getPathExecutorSupplier() {
        return pathExecutorSupplier;
    }

    public static void setPathExecutorSupplier(Supplier<PathExecutor> pathExecutorSupplier) {
        PathExecutor.pathExecutorSupplier = pathExecutorSupplier;
    }

    public boolean execute(Path path) {
        return path.walk(this);
    }

    public boolean handleDoor(Path path, TileEdge tileEdge) {
        SceneObject door = Reachable.findDoor(tileEdge.getTileStart().getPosition());
        if (door == null) {
            door = Reachable.findDoor(tileEdge.getTileEnd().getPosition());
        }
        if (door != null) {
            for (String action : door.getActions()) {
                if (HpaGenerationData.getDoorActions().contains(action.toLowerCase())) {
                    if (interactWithSceneObject(path, door, action)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean handlePlaneChange(Path path, Position position, boolean positivePlaneChange) {
        SceneObject planeChangeObject = getPlaneChangeSceneObject(path, position, positivePlaneChange);
        if (planeChangeObject != null) {
            Set<String> planeChangeActions = HpaGenerationData.getPlaneChangeAction(positivePlaneChange, planeChangeObject.getActions());
            log("Found as " + planeChangeObject + " plane change object with actions " + planeChangeActions + ".");
            for (String planeChangeAction : planeChangeActions) {
                if (interactWithSceneObject(path, planeChangeObject, planeChangeAction)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean interactWithSceneObject(Path path, SceneObject object, String action) {
        if (object == null || action == null) {
            return false;
        }

        object.interact(action);

        if (object.distance() > 5) {
            boolean moving = Time.sleepUntil(() -> Players.getLocal().isMoving(), 50, 1500);
            if (!moving) {
                log("Interaction on " + object.getName() + " with action " + action + " did not result in movement. Walking to scene object.");
                Position position = Movement.getReachableMap().findNearestAccessiblePosition(object);
                if (position != null) {
                    return setWalkFlag(path, position);
                }
            }
            return moving;
        }

        return true;
    }

    protected SceneObject getPlaneChangeSceneObject(Path path, Position position, boolean positiveLevelChange) {
        for (SceneObject sceneObject : SceneObjects.getAt(position)) {
            if (sceneObject == null || sceneObject.getName() == null) {
                continue;
            }
            if (sceneObject.getFloorLevel() != position.getFloorLevel()) {
                continue;
            }
            if (HpaGenerationData.isPlaneChange(positiveLevelChange, sceneObject.getName(), sceneObject.getActions(), sceneObject.getId())) {
                return sceneObject;
            }
        }
        return null;
    }

    public TilePath buildSubPath(Path path, Position subDestination, Position pathDestination) {
        if (randomizeAll || !subDestination.equals(pathDestination)) {
            subDestination = randomizePosition(path, subDestination);
        }
        AStar aStar = new AStar();
        TilePath tilePath = aStar.buildPath(Players.getLocal(), subDestination);
        if (Movement.getDebug().isToggled()) {
            Movement.getDebug().setLastAStar(aStar);
        }
        return tilePath;
    }

    public boolean setWalkFlag(Path path, Position destination) {
        return Movement.setWalkFlagWithConfirm(destination);
    }

    public Position randomizePosition(Path path, Position in) {
        Set<Position> options = new HashSet<>();
        options.add(in);

        Queue<Position> open = new LinkedList<>();
        open.add(in);

        Set<Position> closed = new HashSet<>();
        while (!open.isEmpty()) {
            if (options.size() > 25) {
                break;
            }
            Position poll = open.poll();
            closed.add(poll);

            for (TileEdge tileEdge : new TileNode(poll).getNeighbors()) {
                Position end = tileEdge.getTileEnd().getPosition();
                if (closed.contains(end)) {
                    continue;
                }
                if (tileEdge.getType() == EdgeType.BASIC) {
                    options.add(end);
                    open.add(end);
                }
            }
        }

        return Random.nextElement(options);
    }

    public boolean handleCustomEdge(HpaPath hpaPath, HpaEdge hpaEdge) {
        log("Handling custom edge " + hpaEdge + ".");
        EdgeData edgeData = hpaEdge.getEdgeData();
        if (edgeData != null) {
            List<EdgeInteraction> interactions = edgeData.getInteractions();
            if (interactions != null) {
                for (EdgeInteraction interaction : interactions) {
                    if (!handleCustomInteraction(hpaPath, hpaEdge, interaction)) {
                        return false;
                    }
                }
            } else {
                log("Custom edge had no interactions");
            }
        } else {
            log("Edge data was not present");
        }

        return true;
    }

    public boolean handleCustomInteraction(HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction) {
        if (executors.containsKey(interaction.getType())) {
            log(String.format("Found an interaction of type %d.", interaction.getType()));
            return executors.get(interaction.getType()).handleCustomInteraction(this, hpaPath, hpaEdge, interaction);
        }

        log(String.format("Failed to find interaction %d.", interaction.getType()));
        return false;
    }

    protected void log(String log) {
        if (Movement.getDebug().isToggled()) {
            Log.info("PathExecutor - " + log);
        }
    }

    public PathExecutor setRandomizeAll(boolean randomizeAll) {
        this.randomizeAll = randomizeAll;
        return this;
    }
}
