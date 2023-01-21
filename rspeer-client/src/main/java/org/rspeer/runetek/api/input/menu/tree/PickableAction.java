package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.providers.RSItemDefinition;

public final class PickableAction extends Action {

    public PickableAction(int opcode, int itemId, int sceneX, int sceneY) {
        super(opcode, itemId, sceneX, sceneY);
    }

    public PickableAction(int opcode, Pickable item) {
        this(opcode, item.getId(), item.getSceneX(), item.getSceneY());
    }

    private RSItemDefinition getDefinition() {
        return Definitions.getItem(primary);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[id=").append(primary)
                .append(",x=").append(secondary)
                .append(",y=").append(tertiary);
        RSItemDefinition definition = getDefinition();
        if (definition != null) {
            builder.append(",name=").append(definition.getName());
        }
        builder.append("]");
        return builder.toString();
    }
}
