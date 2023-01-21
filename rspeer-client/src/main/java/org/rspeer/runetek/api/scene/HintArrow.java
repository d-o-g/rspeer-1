package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.providers.RSPathingEntity;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.providers.RSClient;

public final class HintArrow {

    private HintArrow() {
        throw new IllegalAccessError();
    }

    /**
     * @return The {@code Type} of the hint arrow.
     * Valid types could be an Npc, Player, static location or not present
     */
    public static Type getType() {
        return Type.of(Game.getClient().getHintArrowType());
    }

    /**
     * @return {@code true} if a hint arrow is present
     */
    public static boolean isPresent() {
        return getType() != Type.NOT_PRESENT;
    }

    /**
     * @return The {@code Position} of the hint arrow if present, {@code null} otherwise
     */
    public static Position getPosition() {
        switch (getType()) {
            case STATIC: {
                return new Position(Game.getClient().getHintArrowX(),
                        Game.getClient().getHintArrowY(),
                        Scene.getFloorLevel()
                );
            }

            case PLAYER:
            case NPC: {
                return Functions.mapOrDefault(HintArrow::getTarget, PathingEntity::getPosition, null);
            }

            default: {
                return null;
            }
        }
    }

    /**
     * @return The entity that the hint arrow is focused on, or {@code null} if no entity.
     * It is important to note that if this method returns null, it doesn't necessarily
     * denote that no hint arrow is present. Hint arrows do not need to be focused on an
     * entity, they could be focused on a static location.
     * @see HintArrow#getPosition()
     * @see HintArrow#isPresent()
     */
    public static PathingEntity getTarget() {
        Type type = getType();
        if (type != Type.NPC && type != Type.PLAYER) {
            return null;
        }
        RSClient client = Game.getClient();
        int index = type == Type.PLAYER ? client.getHintArrowPlayerIndex() : client.getHintArrowNpcIndex();
        RSPathingEntity entity = null;
        if (type == Type.NPC && index >= 0 && index < Npcs.MAXIMUM_NPC_COUNT) {
            entity = client.getNpc(index);
        } else if (type == Type.PLAYER && index >= 0 && index < Players.MAXIMUM_PLAYER_COUNT) {
            entity = client.getPlayer(index);
        }
        return entity != null ? entity.getWrapper() : null;
    }

    public enum Type {

        NOT_PRESENT(0),
        NPC(1),
        STATIC(2),
        PLAYER(10);

        private final int id;

        Type(int id) {
            this.id = id;
        }

        private static Type of(int id) {
            for (Type type : Type.values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return NOT_PRESENT;
        }
    }
}
