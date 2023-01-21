package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.transportation.FairyRing;
import org.rspeer.runetek.api.scene.Players;

@Deprecated
public class FairyRingExecutor implements CustomEdgeExecutor {

    private static final int[] STAVE_IDS = {772, 9084};
    private static final String FAIRY_CODE = "FAIRY_CODE";

    @Override
    public boolean handleCustomInteraction(PathExecutor pathExecutor, HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction) {
        String code = (String) extractFirstDataValue(FAIRY_CODE, hpaEdge);
        if (code == null) {
            code = (String) extractFirstDataValue(FAIRY_CODE, hpaPath.getNext(hpaEdge));
        }

        if (!Equipment.contains(STAVE_IDS)) {
            Item staff = Inventory.getFirst(STAVE_IDS);
            if (staff != null && staff.interact("Wield")) {
                Time.sleepUntil(() -> Equipment.contains(STAVE_IDS), 1200);
            }

            return false;
        }

        log("Found fairy code of " + code + ".");
        if (code == null) {
            return FairyRing.zanaris();
        } else {
            return handleCode(code);
        }
    }

    private boolean handleCode(String code) {
        boolean result = false;
        if (!FairyRing.isInterfaceOpen()) {
            result = FairyRing.open();
            Time.sleepUntil(FairyRing::isInterfaceOpen, 3500);
        }

        if (FairyRing.isInterfaceOpen()) {
            result = FairyRing.travel(FairyRing.Destination.valueOf(code.toUpperCase()))
                    && Time.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 1500)
                    && Time.sleepUntil(() -> Players.getLocal().getAnimation() == -1, 3500);
        }

        return result;
    }
}
