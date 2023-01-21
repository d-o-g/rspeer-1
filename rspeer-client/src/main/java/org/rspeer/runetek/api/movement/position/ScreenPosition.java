package org.rspeer.runetek.api.movement.position;

import java.awt.*;
import java.util.Objects;

public class ScreenPosition {

    protected final int x, y;

    public ScreenPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "ScreenPosition{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScreenPosition that = (ScreenPosition) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public void draw(Graphics source, String s) {
        source.drawString(s, x, y);
    }
}
