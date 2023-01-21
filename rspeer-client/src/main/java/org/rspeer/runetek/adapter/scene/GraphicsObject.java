package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.movement.position.FinePosition;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.providers.RSAnimationSequence;
import org.rspeer.runetek.providers.RSGraphicsObject;

/**
 * Created by jasper on 02/08/18.
 */
public final class GraphicsObject extends Entity<RSGraphicsObject, GraphicsObject> implements RSGraphicsObject, Identifiable, Positionable {

    public GraphicsObject(RSGraphicsObject provider) {
        super(provider);
    }

    @Override
    public int getSceneX() {
        return getScenePosition().getX();
    }

    @Override
    public int getSceneY() {
        return getScenePosition().getY();
    }

    public ScenePosition getScenePosition() {
        return getFinePosition().toScene();
    }

    public FinePosition getFinePosition() {
        return new FinePosition(getFineX(), getFineY(), getFloorLevel());
    }

    @Override
    public int getId() {
        return provider.getId();
    }

    @Override
    public int getFloorLevel() {
        return provider.getFloorLevel();
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
    public RSAnimationSequence getSequence() {
        return provider.getSequence();
    }

    @Override
    public boolean isFinished() {
        return provider.isFinished();
    }
}
