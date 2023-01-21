package org.rspeer.runetek.api.movement.position;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.scene.Players;

import java.util.Objects;

public class InstancePosition implements Positionable {

    private final int instanceX, instanceY, floorLevel;

    public InstancePosition(int instanceX, int instanceY, int floorLevel) {
        this.instanceX = instanceX;
        this.instanceY = instanceY;
        this.floorLevel = floorLevel;
    }

    public int getX() {
        return instanceX;
    }

    public int getY() {
        return instanceY;
    }

    @Override
    public int getFloorLevel() {
        return floorLevel;
    }

    @Override
    public Position getPosition() {
        Position worldLocation = Players.getLocal().getPosition();
        int worldX = worldLocation.getX() - (worldLocation.getX() % 192) + instanceX;
        int worldY = worldLocation.getY() - (worldLocation.getY() % 192) + instanceY;
        return new Position(worldX, worldY, floorLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InstancePosition that = (InstancePosition) o;
        return instanceX == that.instanceX &&
                instanceY == that.instanceY &&
                floorLevel == that.floorLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceX, instanceY, floorLevel);
    }

    @Override
    public String toString() {
        return "InstancePosition[x=" + instanceX + ", y=" + instanceY + ", level=" + floorLevel + "]";
    }
}
