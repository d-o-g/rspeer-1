package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.scene.Players;

public final class PlayerAction extends PathingEntityAction<Player> {

    public PlayerAction(int opcode, int index) {
        super(opcode, index);
    }

    public PlayerAction(int opcode, Player player) {
        this(opcode, player.getIndex());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[index=")
                .append(getIndex());
        Player player = getSource();
        if (player != null) {
            builder.append(",name=")
                    .append(player.getName())
                    .append(",x=")
                    .append(player.getX())
                    .append("y=")
                    .append(player.getY());
        }
        return builder.append("]").toString();
    }

    public Player getSource() {
        return Players.getAt(getIndex());
    }
}