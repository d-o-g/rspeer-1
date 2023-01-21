package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.Projectile;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ProjectileMoveListener;

public final class ProjectileMoveEvent extends Event<Projectile> {

    private final int targetX;
    private final int targetY;

    public ProjectileMoveEvent(Projectile source, int targetX, int targetY) {
        super(source);
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ProjectileMoveListener) {
            ((ProjectileMoveListener) listener).notify(this);
        }
    }

    public int getTargetX() {
        return (targetX >> 7) + Scene.getBaseX();
    }

    public int getTargetY() {
        return (targetY >> 7) + Scene.getBaseY();
    }

    public Position getTargetPosition() {
        return new Position(getTargetX(), getTargetY(), getSource().getFloorLevel());
    }

    public Position getPosition() {
        return getSource().getPosition();
    }
}
