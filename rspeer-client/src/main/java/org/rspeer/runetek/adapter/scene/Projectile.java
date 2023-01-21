package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.Targeter;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.providers.RSAnimationSequence;
import org.rspeer.runetek.providers.RSProjectile;

public final class Projectile extends Entity<RSProjectile, Projectile>
        implements RSProjectile, Identifiable, Targeter {

    public Projectile(RSProjectile provider) {
        super(provider);
    }

    @Override
    public int getSceneX() {
        return (int) getFineX() >> 7;
    }

    @Override
    public int getSceneY() {
        return (int) getFineY() >> 7;
    }

    @Override
    public int getFloorLevel() {
        return Scene.getFloorLevel();
    }

    @Override
    public int getStartX() {
        return ((provider.getStartX() >> 7) + Scene.getBaseX());
    }

    @Override
    public int getStartY() {
        return ((provider.getStartY() >> 7) + Scene.getBaseY());
    }

    public Position getStartPosition() {
        return new Position(getStartX(), getStartY(), getFloorLevel());
    }

    @Override
    public double getHeightOffset() {
        return provider.getHeightOffset();
    }

    @Override
    public double getSpeed() {
        return provider.getSpeed();
    }

    @Override
    public double getSpeedX() {
        return provider.getSpeedX();
    }

    @Override
    public double getSpeedY() {
        return provider.getSpeedY();
    }

    @Override
    public double getSpeedZ() {
        return provider.getSpeedZ();
    }

    @Override
    public int getEndCycle() {
        return provider.getEndCycle();
    }

    @Override
    public int getId() {
        return provider.getId();
    }

    @Override
    public int getSlope() {
        return provider.getSlope();
    }

    @Override
    public int getStartCycle() {
        return provider.getStartCycle();
    }

    @Override
    public double getFineX() {
        return provider.getFineX();
    }

    @Override
    public double getFineY() {
        return provider.getFineY();
    }

    @Override
    public int getTargetDistance() {
        return provider.getTargetDistance();
    }

    @Override
    public int getTargetIndex() {
        return provider.getTargetIndex();
    }

    @Override
    public int getXRotation() {
        return provider.getXRotation();
    }

    @Override
    public int getYRotation() {
        return provider.getYRotation();
    }

    @Override
    public RSAnimationSequence getSequence() {
        return provider.getSequence();
    }

    @Override
    public boolean isInMotion() {
        return provider.isInMotion();
    }

    @Override
    public Projectile getWrapper() {
        return provider.getWrapper();
    }

    public PathingEntity getTarget() {
        int index = getTargetIndex();
        if (index == 0) {
            return null;
        } else if (index > 0) {
            return Npcs.getAt(index - 1);
        }
        index = -index - 1;
        return index == Game.getClient().getPlayerIndex() ? Players.getLocal() : Players.getAt(index);
    }
}
