package org.rspeer.runetek.api.movement.position;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.api.scene.Scene;

import java.util.Objects;

public class FinePosition implements Positionable {

    private final int fineX, fineY, floorLevel;
    private final int baseX, baseY;

    public FinePosition(int fineX, int fineY, int floorLevel) {
        this(fineX, fineY, floorLevel, Scene.getBaseX(), Scene.getBaseY());
    }

    public FinePosition(int fineX, int fineY, int floorLevel, int baseX, int baseY) {
        this.fineX = fineX;
        this.fineY = fineY;
        this.floorLevel = floorLevel;
        this.baseX = baseX;
        this.baseY = baseY;
    }

    @Override
    public Position getPosition() {
        return getScenePosition().getPosition();
    }

    public ScenePosition getScenePosition() {
        return new ScenePosition(fineX / Projection.TILE_PIXEL_SIZE, fineY / Projection.TILE_PIXEL_SIZE, floorLevel, baseX, baseY);
    }

    @Override
    public ScreenPosition toScreen() {
        return Projection.fineToScreen(fineX, fineY, 0);
    }

    @Override
    public int getFloorLevel() {
        return floorLevel;
    }

    public int getX() {
        return fineX;
    }

    public int getY() {
        return fineY;
    }

    @Override
    public String toString() {
        return "FinePosition[x=" + fineX + ", y=" + fineY + ", level=" + floorLevel + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FinePosition that = (FinePosition) o;
        return fineX == that.fineX &&
                fineY == that.fineY &&
                floorLevel == that.floorLevel &&
                baseX == that.baseX &&
                baseY == that.baseY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fineX, fineY, floorLevel, baseX, baseY);
    }
}
