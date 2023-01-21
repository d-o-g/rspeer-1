package org.rspeer.runetek.api.movement.position;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.api.scene.Scene;

import java.util.Objects;

public class ScenePosition implements Positionable {

    private final int baseX, baseY;
    private final int sceneX;
    private final int sceneY;
    private final int floorLevel;

    public ScenePosition(int sceneX, int sceneY, int floorLevel) {
        this(sceneX, sceneY, floorLevel, Scene.getBaseX(), Scene.getBaseY());
    }

    public ScenePosition(int sceneX, int sceneY, int floorLevel, int baseX, int baseY) {
        this.sceneX = sceneX;
        this.sceneY = sceneY;
        this.floorLevel = floorLevel;
        this.baseX = baseX;
        this.baseY = baseY;
    }

    @Override
    public Position getPosition() {
        return new Position(getX() + getBaseX(), getY() + getBaseY(), getFloorLevel());
    }

    @Override
    public FinePosition toFine() {
        return new FinePosition(getX() * Projection.TILE_PIXEL_SIZE, getY() * Projection.TILE_PIXEL_SIZE, getFloorLevel(), getBaseX(), getBaseY());
    }

    public boolean isLoaded() {
        return getX() >= 0 && getX() < 104 && getY() >= 0 && getY() < 104;
    }

    public int getX() {
        return sceneX;
    }

    public int getY() {
        return sceneY;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    @Override
    public int getFloorLevel() {
        return floorLevel;
    }

    @Override
    public String toString() {
        return "ScenePosition[x=" + sceneX + ", y=" + sceneY + ", level=" + floorLevel + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScenePosition that = (ScenePosition) o;
        return baseX == that.baseX &&
                baseY == that.baseY &&
                sceneX == that.sceneX &&
                sceneY == that.sceneY &&
                floorLevel == that.floorLevel;
    }

    @Override
    public int hashCode() {

        return Objects.hash(baseX, baseY, sceneX, sceneY, floorLevel);
    }
}
