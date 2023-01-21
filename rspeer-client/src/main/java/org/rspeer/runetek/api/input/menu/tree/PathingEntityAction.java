package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.scene.PathingEntity;

public abstract class PathingEntityAction<K extends PathingEntity> extends Action {

    protected PathingEntityAction(int opcode, int index) {
        super(opcode, index, 0, 0);
    }

    public int getIndex() {
        return primary;
    }

    public abstract K getSource();
}
