package org.rspeer.runetek.api.movement.pathfinding.hpa.graph;

import org.rspeer.runetek.api.movement.position.Position;

@Deprecated
public class HpaLocation {

    private int x, y, plane;

    public HpaLocation(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    public HpaLocation() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPlane() {
        return plane;
    }

    public void setPlane(int plane) {
        this.plane = plane;
    }

    @Override
    public String toString() {
        return "HpaLocation{" +
                "x=" + x +
                ", y=" + y +
                ", plane=" + plane +
                '}';
    }

    public Position toPosition() {
        return new Position(x, y, plane);
    }
}
