package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.adapter.Adapter;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.Onymous;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Direction;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.providers.RSDoublyNode;
import org.rspeer.runetek.providers.RSEntity;
import org.rspeer.runetek.providers.RSNode;

public abstract class Entity<P extends RSEntity, K extends Entity<P, K>> extends Adapter<P, K>
        implements RSEntity, Positionable, Identifiable {

    protected Entity(P provider) {
        super(provider);
    }

    @Override
    public long getKey() {
        return provider.getKey();
    }

    @Override
    public RSNode getNext() {
        return provider.getNext();
    }

    @Override
    public RSNode getPrevious() {
        return provider.getPrevious();
    }

    @Override
    public int getHeight() {
        return provider.getHeight();
    }

    @Override
    public RSDoublyNode getNextDoubly() {
        return provider.getNextDoubly();
    }

    @Override
    public RSDoublyNode getPreviousDoubly() {
        return provider.getPreviousDoubly();
    }

    public abstract int getSceneX();

    public abstract int getSceneY();

    public abstract int getFloorLevel();

    public final int getX() {
        return getSceneX() + Scene.getBaseX();
    }

    public final int getY() {
        return getSceneY() + Scene.getBaseY();
    }

    public final Position getPosition() {
        return new Position(getX(), getY(), getFloorLevel());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append("[id=").append(getId()).append("|");
        if (this instanceof Onymous) {
            builder.append("name=").append(getName()).append("|");
        }
        builder.append("position=")
                .append(getX()).append(",")
                .append(getY()).append(",")
                .append(getFloorLevel()).append("]");
        return builder.toString();
    }
}
