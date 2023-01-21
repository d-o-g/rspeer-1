package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.*;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Direction;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.providers.*;

public abstract class PathingEntity<P extends RSPathingEntity, K extends PathingEntity<P, K>>
        extends Entity<P, K> implements RSPathingEntity, Interactable, Identifiable,
        Rotatable, Animable, Targeter, Onymous {

    private static final int DEFAULT_MAX_HEALTHBAR_WIDTH = 30;

    protected PathingEntity(P provider) {
        super(provider);
    }

    @Override
    public void addHitSplat(int type, int amount, int id, int special, int startCycle, int endCycle) {
        provider.addHitSplat(type, amount, id, special, startCycle, endCycle);
    }

    @Override
    public void addHitUpdate(int id, int startCycle, int currentWidth, int duration, int startWidth, int currentCycle) {
        provider.addHitUpdate(id, startCycle, currentWidth, duration, startWidth, currentCycle);
    }

    @Override
    public byte getHitsplatCount() {
        return provider.getHitsplatCount();
    }

    public boolean isAnimating() {
        return getAnimation() != -1; //|| getAnimationFrame() > 0;
    }

    @Override
    public int getAnimation() {
        return provider.getAnimation();
    }

    @Override
    public int getAnimationDelay() {
        return provider.getAnimationDelay();
    }

    @Override
    public int getAnimationFrame() {
        return provider.getAnimationFrame();
    }

    @Override
    public int getGraphic() {
        return provider.getGraphic();
    }

    @Override
    public int getWalkingStance() {
        return provider.getWalkingStance();
    }

    @Override
    public int getOrientation() {
        return provider.getOrientation();
    }

    @Override
    public int getPathQueueSize() {
        return provider.getPathQueueSize();
    }

    @Override
    public int getStance() {
        return provider.getStance();
    }

    @Override
    public int getAnimationFrameCycle() {
        return provider.getAnimationFrameCycle();
    }

    @Override
    public int getFineX() {
        return provider.getFineX();
    }

    @Override
    public int getFineY() {
        return provider.getFineY();
    }

    @Override
    public int getStanceFrame() {
        return provider.getStanceFrame();
    }

    @Override
    public int getTargetIndex() {
        return provider.getTargetIndex();
    }

    @Override
    public RSLinkedList getHealthBars() {
        return provider.getHealthBars();
    }

    @Override
    public byte[] getPathQueueTraversed() {
        return provider.getPathQueueTraversed();
    }

    @Override
    public int[] getHitsplatCycles() {
        return provider.getHitsplatCycles();
    }

    @Override
    public int[] getHitsplatIds() {
        return provider.getHitsplatIds();
    }

    @Override
    public int[] getHitsplatTypes() {
        return provider.getHitsplatTypes();
    }

    @Override
    public int[] getHitsplats() {
        return provider.getHitsplats();
    }

    @Override
    public int[] getPathXQueue() {
        return provider.getPathXQueue();
    }

    @Override
    public int[] getPathYQueue() {
        return provider.getPathYQueue();
    }

    @Override
    public int[] getSpecialHitsplats() {
        return provider.getSpecialHitsplats();
    }

    public abstract String getName();

    public abstract int getCombatLevel();

    public final int getSceneX() {
        return getFineX() >> 7;
    }

    public final int getSceneY() {
        return getFineY() >> 7;
    }

    public final int getFloorLevel() {
        return Scene.getFloorLevel();
    }

    public int getIndex() {
        return provider.getIndex();
    }

    public final PathingEntity getTarget() {
        int index = getTargetIndex();
        if (index == -1) {
            return null;
        } else if (index < 32768) {
            return Npcs.getAt(index);
        }
        index -= 32768;
        return index == Game.getClient().getPlayerIndex() ? Players.getLocal() : Players.getAt(index);
    }

    public final boolean isMoving() {
        return getPathQueueSize() > 0;
    }

    public boolean isHealthBarVisible() {
        for (int cycle : provider.getHitsplatCycles()) {
            if (cycle + 250 > Game.getEngineCycle()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the direction this PathingEntity is facing in. Returns null only if the orientation value is non-sensical.
     * @return the direction this PathingEntity is facing
     */
    public Direction getDirection() {
        switch (getOrientation() /  256) {
            case 0: return Direction.SOUTH;
            case 1: return Direction.SOUTH_WEST;
            case 2: return Direction.WEST;
            case 3: return Direction.NORTH_WEST;
            case 4: return Direction.NORTH;
            case 5: return Direction.NORTH_EAST;
            case 6: return Direction.EAST;
            case 7: return Direction.SOUTH_EAST;
            default: return null;
        }
    }

    /**
     * <p>
     * Checks if the PathingEntity is facing any positionable directly.<br />
     * <b>SceneObjects:</b> For SceneObjects this method will check the object area to see if you are facing any of the tiles
     * <b>Other:</b> For any other type of positionable this method will just check if the positionable tile is equal to the faced tile
     * </p>
     * @param positionable the positionable instance which needs to be checked
     * @return true if the entity is directly facing the positionable
     */
    public boolean isFacing(Positionable positionable) {
        Direction direction = getDirection();
        if (direction == null) {
            return false;
        }

        Position offset = getPosition().translate(direction.getXOff(), direction.getYOff());

        if (positionable instanceof SceneObject) {
            SceneObject object = (SceneObject) positionable;
            return object.getArea().contains(offset);
        } else if (positionable instanceof Npc) {
            Npc npc = (Npc) positionable;
            return npc.getArea().contains(offset);
        }

        return offset.equals(positionable.getPosition());
    }

    @Override
    public String getOverheadText() {
        return Functions.mapOrDefault(() -> provider, RSPathingEntity::getOverheadText, "");
    }

    public RSHealthBar getHealthBar() {
        RSLinkedList bars = getHealthBars();
        if (bars != null) {
            RSNode next = bars.getSentinel().getNext();
            if (next instanceof RSHealthBar) {
                return (RSHealthBar) next;
            }
        }
        return null;
    }

    private int getMaxHealthBarWidth() {
        RSHealthBar peer = getHealthBar();
        if (peer.getDefinition() == null) {
            return DEFAULT_MAX_HEALTHBAR_WIDTH;
        }
        return peer.getDefinition().getMaxWidth();
    }

    /**
     * Note: This method will only return correctly when the health bar is present.
     * For local player health it is recommended that you use the Health class instead
     *
     * @return The current health percent
     */
    public int getHealthPercent() {
        RSHealthBar peer = getHealthBar();
        if (peer != null) {
            RSNode update = peer.getHitsplats().getSentinel().getNext();
            if (update instanceof RSHitUpdate) {
                int max = getMaxHealthBarWidth();
                int width = ((RSHitUpdate) update).getStartWidth();
                return (int) Math.ceil(100.0 * width / max);
            }
        }
        return 100;
    }
}
