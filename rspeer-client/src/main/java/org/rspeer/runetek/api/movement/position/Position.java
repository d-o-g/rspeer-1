package org.rspeer.runetek.api.movement.position;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Direction;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.api.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a position relative to the world
 */
public class Position implements Positionable {

    private final int worldX;
    private final int worldY;
    private final int floorLevel;

    public Position(int hash) {
        this(hash >> 14 & 0x3fff, hash & 0x3fff, hash >> 28 & 0x3);
    }

    /**
     * Constructs a {@code Position}
     *
     * @param worldX     The x position relative to the world
     * @param worldY     The y position relative to the world
     * @param floorLevel The floor level
     */
    public Position(int worldX, int worldY, int floorLevel) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.floorLevel = floorLevel;
    }

    /**
     * Constructs a {@code Position}, defaulting the floor level to 0
     *
     * @param worldX The x position relative to the world
     * @param worldY The y position relative to the world
     */
    public Position(int worldX, int worldY) {
        this(worldX, worldY, 0);
    }

    /**
     * @return Converts this position to a {@code ScenePosition}
     */
    public ScenePosition toScene() {
        return new ScenePosition(getX() - Scene.getBaseX(), getY() - Scene.getBaseY(), getFloorLevel());
    }

    /**
     * @return Converts this position to a {@code InstancePosition}
     */
    public InstancePosition toInstance() {
        return new InstancePosition(getX() % 192, getY() % 192, getFloorLevel());
    }

    /**
     * @return The floor level
     */
    @Override
    public int getFloorLevel() {
        return floorLevel;
    }

    /**
     * @return The x position, relative to the world
     */
    public int getX() {
        return worldX;
    }

    /**
     * @return The y position, relative to the world
     */
    public int getY() {
        return worldY;
    }

    public Position translate(int x, int y) {
        return new Position(getX() + x, getY() + y, getFloorLevel());
    }

    public Position randomize(int offset) {
        return translate(Random.nextInt(-offset, offset), Random.nextInt(-offset, offset));
    }

    @Override
    public Position getPosition() {
        return this;
    }

    public boolean isLoaded() {
        int x = getX() - Scene.getBaseX(), y = getY() - Scene.getBaseY();
        return x >= 0 && x < 104 && y >= 0 && y < 104;
    }

    /**
     * Gets the neighbouring tiles from this position.
     *
     * @param diagonal <p>
     *                 used to indicate whether or not you want to return the diagonal tiles: {@link Direction#NORTH_EAST},
     *                 {@link Direction#NORTH_WEST}, {@link Direction#SOUTH_EAST} and {@link Direction#SOUTH_WEST}
     *                 </p>
     * @return the list of positions neighbouring this position
     */
    public List<Position> getNeighbors(boolean diagonal) {
        List<Position> positions = new ArrayList<>(diagonal ? 8 : 4);

        for (Direction direction : Direction.values()) {
            if (!diagonal && !direction.isCardinal()) {
                continue;
            }

            positions.add(translate(direction.getXOff(), direction.getYOff()));
        }

        return positions;
    }

    /**
     * Calls {@link #getNeighbors(boolean)} with boolean diagonal false.
     *
     * @return the list of positions neighbouring this position without the diagonal tiles
     */
    public List<Position> getNeighbors() {
        return getNeighbors(false);
    }

    public int getRegionId() {
        return (worldX / 64 & 0xff) << 8 | worldY / 64 & 0xff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return worldX == position.worldX &&
                worldY == position.worldY &&
                floorLevel == position.floorLevel;
    }

    @Override
    public int hashCode() {
        return floorLevel << 28 | worldX << 14 | worldY;
    }

    @Override
    public String toString() {
        return "Position[x=" + worldX + ", y=" + worldY + ", level=" + floorLevel + "]";
    }

    public void outline(Graphics source) {
        Polygon tileShape = Projection.getTileShape(this);
        if (tileShape != null) {
            source.drawPolygon(tileShape);
        }
    }
}
