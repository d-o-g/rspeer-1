package org.rspeer.runetek.api.movement.position;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.scene.Scene;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * Created by Spencer on 15/02/2018.
 */
public abstract class Area {

    private final int floorLevel;
    protected boolean ignoreFloorLevel = false;

    protected Area(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    public static Area rectangular(Position start, Position end, int floorLevel) {
        return new Rectangular(start, end, floorLevel);
    }

    public static Area rectangular(Position start, Position end) {
        return rectangular(start, end, start.getFloorLevel());
    }

    public static Area rectangular(int minX, int minY, int maxX, int maxY, int floorLevel) {
        return rectangular(new Position(minX, minY), new Position(maxX, maxY), floorLevel);
    }

    public static Area rectangular(int minX, int minY, int maxX, int maxY) {
        return rectangular(minX, minY, maxX, maxY, 0);
    }

    public static Area surrounding(Position origin, int distance, int floorLevel) {
        return rectangular(origin.getX() - distance, origin.getY() - distance, origin.getX() + distance, origin.getY() + distance, floorLevel);
    }

    public static Area surrounding(Position origin, int distance) {
        return surrounding(origin, distance, origin.getFloorLevel());
    }

    public static Area absolute(int floorLevel, Position... positions) {
        return new Absolute(floorLevel, positions);
    }

    public static Area absolute(Position... positions) {
        return absolute(Scene.getFloorLevel(), positions);
    }

    public static Area polygonal(int floorLevel, Position... tiles) {
        if (tiles.length < 3) {
            throw new IllegalArgumentException("You must provide at least 3 positions to create a polygonal area");
        }
        return new Polygonal(floorLevel, tiles);
    }

    public static Area polygonal(Position... tiles) {
        if (tiles.length < 3) {
            throw new IllegalArgumentException("Must specify more positions to create a polygonal area");
        }
        return polygonal(tiles[0].getFloorLevel(), tiles);
    }

    public static Area singular(Position tile) {
        return new Singular(tile);
    }

    public abstract List<Position> getTiles();

    public Area translate(int x, int y) {
        throw new UnsupportedOperationException("Override");
    }

    public abstract boolean contains(Positionable positionable);

    public Position getCenter() {
        List<Position> tiles = getTiles();
        int x = 0, y = 0;
        for (Position t : tiles) {
            x += t.getX();
            y += t.getY();
        }
        x /= tiles.size();
        y /= tiles.size();
        return new Position(x, y, floorLevel);
    }

    public void outline(Graphics g) {
        for (Position position : getTiles()) {
            position.outline(g);
        }
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public final Area setIgnoreFloorLevel(boolean ignoreFloorLevel) {
        this.ignoreFloorLevel = ignoreFloorLevel;
        return this;
    }

    public boolean isIgnoringFloorLevel() {
        return ignoreFloorLevel;
    }

    public static class Absolute extends Area {

        private final List<Position> positions;

        private Absolute(int floorLevel, Position... positions) {
            super(floorLevel);
            this.positions = Arrays.asList(positions);
        }

        @Override
        public List<Position> getTiles() {
            return positions;
        }

        @Override
        public boolean contains(Positionable positionable) {
            for (Position pos : positions) {
                if (positionable.getPosition().equals(pos)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class Polygonal extends Area {

        private final Polygon polygon;
        private final List<Position> tiles;

        private Polygonal(int floorLevel, Position... tiles) {
            super(floorLevel);
            this.tiles = new ArrayList<>();
            Collections.addAll(this.tiles, tiles);
            polygon = new Polygon();
            for (Position pos : tiles) {
                polygon.addPoint(pos.getX(), pos.getY());
            }
        }

        @Override
        public List<Position> getTiles() {
            return tiles;
        }

        @Override
        public boolean contains(Positionable p) {
            return p != null && (super.ignoreFloorLevel || p.getFloorLevel() == getFloorLevel()) && polygon.contains(p.getX(), p.getY());
        }

        @Override
        public void outline(Graphics gr) {
            Graphics2D g = (Graphics2D) gr;
            g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            Polygon screenPolygon = new Polygon();
            for (Position pos : tiles) {
                ScreenPosition screen = pos.toScreen();
                if (screen != null) {
                    screenPolygon.addPoint(screen.getX(), screen.getY());
                }
            }
            g.setColor(Color.RED.darker());
            g.fillPolygon(screenPolygon);

            g.setColor(Color.BLACK);
            g.drawPolygon(screenPolygon);
        }

        @Override
        public Area translate(int x, int y) {
            Position[] tiles = this.tiles.toArray(new Position[0]);
            for (int i = 0; i < tiles.length; i++) {
                Position curr = tiles[i];
                tiles[i] = curr.translate(x, y);
            }
            return new Polygonal(getFloorLevel(), tiles)
                    .setIgnoreFloorLevel(super.ignoreFloorLevel);
        }
    }

    private static class Rectangular extends Area {

        private final List<Position> tiles;
        private final int minX, maxX, minY, maxY;

        private Rectangular(Position start, Position end, int floorLevel) {
            super(floorLevel);
            tiles = new ArrayList<>();

            int startX = start.getX(), startY = start.getY();
            int endX = end.getX(), endY = end.getY();
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                for (int y = Math.max(startY, endY); y >= Math.min(startY, endY); y--) {
                    tiles.add(new Position(x, y, floorLevel));
                }
            }
            minX = Math.min(startX, endX);
            minY = Math.min(startY, endY);
            maxX = Math.max(startX, endX);
            maxY = Math.max(startY, endY);
        }

        @Override
        public List<Position> getTiles() {
            return tiles;
        }

        @Override
        public boolean contains(Positionable p) {
            return p != null && (super.ignoreFloorLevel || p.getFloorLevel() == getFloorLevel())
                    && p.getX() >= minX && p.getY() >= minY
                    && p.getX() <= maxX && p.getY() <= maxY;
        }

        @Override
        public Area translate(int x, int y) {
            Position start = new Position(minX, minY, getFloorLevel()).translate(x, y);
            Position end = new Position(maxX, maxY, getFloorLevel()).translate(x, y);
            return new Rectangular(start, end, getFloorLevel())
                    .setIgnoreFloorLevel(super.ignoreFloorLevel);
        }
    }

    private static class Singular extends Area {

        private final Position src;

        private Singular(Position src) {
            super(src.getFloorLevel());
            this.src = src;
        }

        @Override
        public List<Position> getTiles() {
            return Collections.singletonList(src);
        }

        @Override
        public boolean contains(Positionable p) {
            return p != null && p.getPosition().equals(src);
        }
    }
}