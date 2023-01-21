package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.transportation.CharterShip;
import org.rspeer.runetek.api.scene.Players;

@Deprecated
public class CharterShipExecutor implements CustomEdgeExecutor {

    private static final String DESTINATION = "DESTINATION";

    @Override
    public boolean handleCustomInteraction(PathExecutor pathExecutor, HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction) {
        String dest = (String) extractFirstDataValue(DESTINATION, hpaEdge);

        if (dest != null) {
            Position at = hpaEdge.getStart().getPosition();
            log(at.distance() + " ");
            if (at.distance() < 8) {
                if (CharterShip.open()
                        && Time.sleepUntil(CharterShip::isInterfaceOpen, 3500)
                        && CharterShip.charter(CharterShip.Destination.valueOf(dest.toUpperCase().replace(" ", "_")))) {
                    return true;
                }
            } else {
                Movement.setWalkFlag(at);
                Time.sleepUntil(() -> Players.getLocal().isMoving(), 1200);
                return true;
            }
        }

        return false;
    }
}
