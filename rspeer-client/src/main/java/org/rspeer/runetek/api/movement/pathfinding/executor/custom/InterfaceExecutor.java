package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;

@Deprecated
public class InterfaceExecutor implements CustomEdgeExecutor {

    private static final String PATH = "PATH";
    private static final String ACTION = "ACTION";

    @Override
    public boolean handleCustomInteraction(PathExecutor pathExecutor, HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction) {
        String path = (String) extractDataValue(PATH, interaction);
        if (path == null) {
            return false;
        }

        String[] split = path.split(">");
        InterfaceComponent interact = getInterfaceComponent(split);
        if (interact == null) {
            return false;
        }

        String action = (String) extractDataValue(ACTION, interaction);

        if (action != null) {
            return interact.interact(action);
        } else {
            return interact.interact(ActionOpcodes.INTERFACE_ACTION);
        }
    }

    private InterfaceComponent getInterfaceComponent(String[] path) {
        if (path.length < 2) {
            return null;
        }

        InterfaceComponent base = Interfaces.getComponent(Integer.parseInt(path[0]), Integer.parseInt(path[1]));
        for (int i = 2; i < path.length; i++) {
            if (base != null) {
                base = base.getComponent(i);
            }
        }

        return base;
    }
}
