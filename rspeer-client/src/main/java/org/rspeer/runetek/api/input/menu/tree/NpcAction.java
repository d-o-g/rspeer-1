package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.scene.Npcs;

public final class NpcAction extends PathingEntityAction<Npc> {

    public NpcAction(int opcode, int index) {
        super(opcode, index);
    }

    public NpcAction(int opcode, Npc npc) {
        this(opcode, npc.getIndex());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[index=")
                .append(getIndex());
        Npc npc = getSource();
        if (npc != null) {
            builder.append(",name=")
                    .append(npc.getName())
                    .append(",id=")
                    .append(npc.getId())
                    .append(",x=")
                    .append(npc.getX())
                    .append(",y=")
                    .append(npc.getY());
        }
        return builder.append("]").toString();
    }

    public Npc getSource() {
        return Npcs.getAt(getIndex());
    }
}
