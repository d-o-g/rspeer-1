package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileEdge;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileNode;
import org.rspeer.runetek.api.movement.pathfinding.region.util.CollisionFlags;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSSceneGraph;
import org.rspeer.runetek.providers.RSTile;

import java.util.*;

public final class Scene {

    /**
     * The size of the Scene in tiles
     **/
    public static final int SIZE = 104;
    private static long lastCollisionUpdate;

    private Scene() {
        throw new IllegalAccessError();
    }

    /**
     * @return The current floor level being rendered in the scene
     */
    public static int getFloorLevel() {
        return Game.getClient().getFloorLevel();
    }

    /**
     * @return The scene render rules
     */
    public static byte[][][] getRenderRules() {
        return Game.getClient().getSceneRenderRules();
    }

    /**
     * @return The tile heights - {@code heights[z][x][y]}
     */
    public static int[][][] getTileHeights() {
        return Game.getClient().getTileHeights();
    }

    /**
     * @return The base X position of the scene
     */
    public static int getBaseX() {
        return Game.getClient().getBaseX();
    }

    /**
     * @return The base Y position of the scene
     */
    public static int getBaseY() {
        return Game.getClient().getBaseY();
    }

    /**
     * @return The base position of the scene
     */
    public static Position getBase() {
        return new Position(getBaseX(), getBaseY(), getFloorLevel());
    }

    /**
     * @return The current SceneGraph
     */
    public static RSSceneGraph getCurrent() {
        return Game.getClient().getSceneGraph();
    }

    /**
     * @param sceneX     The x position, relative to the scene
     * @param sceneY     The y position, relative to the scene
     * @param floorLevel The floor level
     * @return An RSTile at the given coordinates
     */
    public static RSTile getTile(int sceneX, int sceneY, int floorLevel) {
        if (sceneX > SIZE || sceneX < 0 || sceneY > SIZE || sceneY < 0 || floorLevel < 0 || floorLevel > 3) {
            throw new IllegalArgumentException("Coordinates outside loaded scene.");
        }
        RSSceneGraph scene = getCurrent();
        if (scene != null) {
            return scene.getTiles()[floorLevel][sceneX][sceneY];
        }
        return null;
    }

    public static boolean isDynamic() {
        return Game.getClient().isInInstancedScene();
    }

    public static int[][][] getDynamic() {
        return Game.getClient().getDynamicSceneData();
    }

    /**
     * @param x          The fine x position
     * @param y          The fine y positon
     * @param floorLevel The floor level
     * @return Calculates the height of a tile at the given coordinates
     */
    public static int getGroundHeight(int x, int y, int floorLevel) {
        int x1 = x >> 7;
        int y1 = y >> 7;
        if (x1 < 0 || x1 > SIZE || y1 < 0 || y1 > SIZE) {
            return 0;
        }
        byte[][][] rules = Scene.getRenderRules();
        if (rules == null) {
            return 0;
        }
        int[][][] heights = Scene.getTileHeights();
        if (heights == null) {
            return 0;
        }
        if (floorLevel < 3 && (rules[1][x1][y1] & 0x2) == 2) {
            floorLevel++;
        }
        int x2 = x & 0x7F;
        int y2 = y & 0x7F;
        int h1 = heights[floorLevel][x1][y1] * (Projection.TILE_PIXEL_SIZE - x2) + heights[floorLevel][x1 + 1][y1] * x2 >> 7;
        int h2 = heights[floorLevel][x1][y1 + 1] * (Projection.TILE_PIXEL_SIZE - x2) + heights[floorLevel][x1 + 1][y1 + 1] * x2 >> 7;
        return h1 * (Projection.TILE_PIXEL_SIZE - y2) + h2 * y2 >> 7;
    }

    public static int[][] getCollisionFlags() {
        return Game.getClient().getCollisionMaps()[Players.getLocal().getPosition().getFloorLevel()].getFlags();
    }

    public static int getCollisionFlag(Position position) {
        ScenePosition scenePosition = position.toScene();
        int[][] collisionFlags = getCollisionFlags();
        int x = scenePosition.getX();

        if (x >= 0 && x < collisionFlags.length) {
            int[] yArray = collisionFlags[x];
            int y = scenePosition.getY();
            if (y >= 0 && y < yArray.length) {
                return yArray[y];
            }
        }

        return CollisionFlags.UNINITIALIZED;
    }

    public static Position findUnblocked(Position position) {
        if (position == null) {
            return null;
        }
        if (!isBlocked(position)) {
            return position;
        }

        Set<TileNode> closed = new HashSet<>();
        Queue<TileNode> open = new LinkedList<>();

        open.add(new TileNode(position));

        int attempts = 0;
        while (!open.isEmpty()) {
            if (attempts++ > 100) {
                break;
            }

            TileNode poll = open.poll();
            closed.add(poll);

            for (TileEdge tileEdge : poll.getNeighbors(true)) {
                if (tileEdge.getType() != EdgeType.BASIC) {
                    continue;
                }
                if (!isBlocked(tileEdge.getTileEnd().getPosition())) {
                    return tileEdge.getTileEnd().getPosition();
                }
                if (!closed.contains(tileEdge.getTileEnd())) {
                    open.add(tileEdge.getTileEnd());
                }
            }
        }

        return null;
    }

    public static boolean isBlocked(Position position) {
        return CollisionFlags.isBlocked(getCollisionFlag(position));
    }

    public static boolean isLoaded(Positionable positionable) {
        Objects.requireNonNull(positionable);
        return !CollisionFlags.check(getCollisionFlag(positionable.getPosition()), CollisionFlags.UNINITIALIZED);
    }

    public static long getLastCollisionUpdate() {
        return lastCollisionUpdate;
    }

    public static void setLastCollisionUpdate(long lastCollisionUpdate) {
        Scene.lastCollisionUpdate = lastCollisionUpdate;
    }

    /**
     * Performs a check on the current loaded map region ids to see if your specified id is loaded.
     * Useful for telling if you are in a region or not. Please note that the region doesn't have to contain your player,
     * it only has to be loaded.
     *
     * @param regionId the region ID you want to check
     * @return true if the passed region ID is currently loaded
     */
    public static boolean isRegionLoaded(int regionId) {
        for (int region : Game.getClient().getMapRegions()) {
            if (region == regionId) {
                return true;
            }
        }
        return false;
    }

    public static int[] getLoadedRegions() {
        return Functions.mapOrDefault(Game::getClient, RSClient::getMapRegions, new int[0]);
    }

    public static int getCurrentRegion() {
        Position pos = Players.getLocal().getPosition();
        return (pos.getX() / 64 & 0xff) << 8 | pos.getY() / 64 & 0xff;
    }
}
