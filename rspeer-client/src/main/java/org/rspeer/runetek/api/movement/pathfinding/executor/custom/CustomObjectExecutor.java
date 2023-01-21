package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaNode;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;

import java.util.Comparator;
import java.util.function.Predicate;

@Deprecated
public class CustomObjectExecutor implements CustomEdgeExecutor {

    private static final String OBJECT_NAME = "OBJECT_NAME";
    private static final String OBJECT_ACTION = "OBJECT_ACTION";
    private static final String OBJECT_ID = "OBJECT_ID";
    private static final String SELECTION_MODE = "SELECTION_MODE";

    private static final int ON_TOP = 0;
    private static final int NEAREST_TO_PLAYER = 2;

    @Override
    public boolean handleCustomInteraction(PathExecutor pathExecutor, HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction) {
        String object = (String) extractFirstDataValue(OBJECT_NAME, hpaEdge);
        String action = (String) extractFirstDataValue(OBJECT_ACTION, hpaEdge);
        Double selection = (Double) extractFirstDataValue(SELECTION_MODE, hpaEdge);

        SceneObject sceneObject;
        if (object == null) {
            Integer id = (Integer) extractFirstDataValue(OBJECT_ID, hpaEdge);
            log("Looking for object with [id=" + id + ", selection=" + selection + ", action=" + action + "].");
            sceneObject = getSceneObject(id, action, hpaEdge.getStart(), selection.intValue());
        } else {
            log("Looking for object with [name=" + object + ", selection=" + selection + ", action=" + action + "].");
            sceneObject = getSceneObject(object, action, hpaEdge.getStart(), selection.intValue());
        }

        if (sceneObject != null && pathExecutor.interactWithSceneObject(hpaPath, sceneObject, action)) {
            log("Performing " + action + " on object " + sceneObject.getName() + ".");
            Time.sleep(1000, 1400);
            return true;
        }

        return false;
    }

    private SceneObject getSceneObject(String object, String action, HpaNode node, int selection) {
        Predicate<SceneObject> basePredicate = e -> e.getName().equals(object)
                && e.containsAction(action);
        return getSceneObject(basePredicate, node, selection);
    }

    private SceneObject getSceneObject(int object, String action, HpaNode node, int selection) {
        Predicate<SceneObject> basePredicate = e -> e.getId() == object
                && e.containsAction(action);
        return getSceneObject(basePredicate, node, selection);
    }

    private SceneObject getSceneObject(Predicate<SceneObject> basePredicate, HpaNode node, int selection) {
        Position nodePosition = node.getPosition();

        if (selection == ON_TOP) {
            return Predicates.firstMatching(basePredicate, SceneObjects.getAt(nodePosition));
        } else if (selection == NEAREST_TO_PLAYER) {
            return Predicates.firstMatching(basePredicate,
                    SceneObjects.getSorted(Comparator.comparingDouble(Positionable::distance),
                            e -> e.getFloorLevel() == nodePosition.getFloorLevel()));
        } else {
            //NEAREST_TO_START
            return Predicates.firstMatching(basePredicate,
                    SceneObjects.getSorted(Comparator.comparingDouble(e -> e.distance(nodePosition)),
                            e -> e.getFloorLevel() == nodePosition.getFloorLevel()));
        }
    }
}
