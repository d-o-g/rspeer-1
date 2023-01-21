package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeData;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.ui.Log;

import java.util.List;

@Deprecated
public interface CustomEdgeExecutor {

    boolean handleCustomInteraction(PathExecutor executor, HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction);

    default Object extractFirstDataValue(String key, HpaEdge edge) {
        if (edge == null) {
            return null;
        }
        EdgeData edgeData = edge.getEdgeData();
        if (edgeData == null) {
            return null;
        }
        List<EdgeInteraction> interactions = edgeData.getInteractions();
        if (interactions == null) {
            return null;
        }
        for (EdgeInteraction edgeInteraction : edgeData.getInteractions()) {
            if (edgeInteraction.getData().containsKey(key)) {
                return edgeInteraction.getData().get(key);
            }
        }
        return null;
    }

    default Object extractDataValue(String key, EdgeInteraction interaction) {
        if (interaction == null) {
            return null;
        }

        if (interaction.getData().containsKey(key)) {
            return interaction.getData().get(key);
        }

        return null;
    }

    default void log(String value) {
        if (Movement.getDebug().isToggled()) {
            Log.info("CustomEdgeExecutor - " + value);
        }
    }

}
