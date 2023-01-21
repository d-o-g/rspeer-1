package org.rspeer.runetek.event.types;

import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ObjectSpawnListener;

/**
 * Created by Spencer on 30/01/2018.
 */
public final class ObjectSpawnEvent extends Event {

    private final int floorLevel, sceneX, sceneY, stubType, id, type, orientation, spawnDelay, hitpoints;

    public ObjectSpawnEvent(long delay, int floorLevel, int sceneX, int sceneY, int stubType, int id, int type, int orientation, int spawnDelay, int hitpoints) {
        super("Static", delay);
        this.floorLevel = floorLevel;
        this.sceneX = sceneX;
        this.sceneY = sceneY;
        this.stubType = stubType;
        this.id = id;
        this.type = type;
        this.orientation = orientation;
        this.spawnDelay = spawnDelay;
        this.hitpoints = hitpoints;
    }

    public ObjectSpawnEvent(int floorLevel, int sceneX, int sceneY, int stubType, int id, int type, int orientation, int spawnDelay, int hitpoints) {
        this(0, floorLevel, sceneX, sceneY, stubType, id, type, orientation, spawnDelay, hitpoints);
    }

    public Position getPosition() {
        return new Position(Scene.getBaseX() + sceneX, Scene.getBaseY() + sceneY, floorLevel);
    }

    public int getStubType() {
        return stubType;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ObjectSpawnListener) {
            ((ObjectSpawnListener) listener).notify(this);
        }
    }
}
