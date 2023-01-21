package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.input.Camera;
import org.rspeer.runetek.api.movement.position.FinePosition;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.api.movement.position.ScreenPosition;

import java.awt.*;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 6/9/2017.
 */
public final class Projection {

    public static final Dimension APPLET_SIZE = new Dimension(765, 503);
    public static final int TILE_PIXEL_SIZE = 128;
    public static final int[] SINE = new int[2048];
    public static final int[] COSINE = new int[2048];
    public static int mode = 0;
    private static boolean modelRenderingEnabled = true;
    private static boolean landscapeRenderingEnabled = true;
    private static int tickDelay = -1;

    static {
        for (int i = 0; i < SINE.length; i++) {
            SINE[i] = (int) (65536.0D * Math.sin((double) i * 0.0030679615D));
            COSINE[i] = (int) (65536.0D * Math.cos((double) i * 0.0030679615D));
        }
    }

    private Projection() {
        throw new IllegalAccessError();
    }

    /**
     * @param fineX  The fine x position to translate
     * @param fineY  The fine y position to translate
     * @param height The height offset
     * @return Translates an absolute position on the viewport to a {@code ScreenPosition}
     */
    public static ScreenPosition fineToScreen(int fineX, int fineY, int height) {
        return optFineToScreen(fineX, fineY, height).orElse(null);
    }

    public static Optional<ScreenPosition> optFineToScreen(int fineX, int fineY, int height) {
        if (fineX >= TILE_PIXEL_SIZE && fineX <= 13056 && fineY >= TILE_PIXEL_SIZE && fineY <= 13056) {
            int alt = Camera.getPitch();
            if (alt < 0) {
                return Optional.empty();
            }
            int yaw = Camera.getYaw();
            if (yaw < 0) {
                return Optional.empty();
            }
            int elevation = Scene.getGroundHeight(fineX, fineY, Scene.getFloorLevel()) - height;
            fineX -= Camera.getX();
            fineY -= Camera.getY();
            elevation -= Camera.getZ();
            int altSin = SINE[alt];
            int altCos = COSINE[alt];
            int yawSin = SINE[yaw];
            int yawCos = COSINE[yaw];
            int angle = fineY * yawSin + fineX * yawCos >> 16;
            fineY = fineY * yawCos - fineX * yawSin >> 16;
            fineX = angle;
            angle = elevation * altCos - fineY * altSin >> 16;
            fineY = elevation * altSin + fineY * altCos >> 16;
            if (fineY == 0) {
                return Optional.empty();
            }

            int w = Game.getClient().getViewportWidth();
            int h = Game.getClient().getViewportHeight();
            int z = Game.getClient().getViewportScale();

            int x = w / 2 + fineX * z / fineY;
            int y = h / 2 + angle * z / fineY;

            if (x > 0 && x < w && y > 0 && y < h) {
                return Optional.of(new ScreenPosition(x, y));
            }
        }
        return Optional.empty();
    }

    /**
     * @return {@code true} if model rendering is enabled, {@code false} otherwise
     * @see #setLandscapeRenderingEnabled
     */
    public static boolean isModelRenderingEnabled() {
        return modelRenderingEnabled;
    }

    /**
     * This method sets the clients model rendering. If it is set to false, then
     * models will not be rendered. This is set to true when low cpu mode is enabled
     *
     * @param modelRenderingEnabled Whether or not model rendering is enabled
     */
    public static void setModelRenderingEnabled(boolean modelRenderingEnabled) {
        Projection.modelRenderingEnabled = modelRenderingEnabled;
    }

    /**
     * @return {@code true} if model rendering is enabled, {@code false} otherwise
     * @see #setLandscapeRenderingEnabled
     */
    public static boolean isLandscapeRenderingEnabled() {
        return landscapeRenderingEnabled;
    }

    /**
     * This method sets the clients landscape rendering. If it is set to false, then
     * scenery will not be rendered. This is set to true when low cpu mode is enabled
     *
     * @param landscapeRenderingEnabled Whether or not landscape rendering is enabled
     */
    public static void setLandscapeRenderingEnabled(boolean landscapeRenderingEnabled) {
        Projection.landscapeRenderingEnabled = landscapeRenderingEnabled;
    }

    /**
     * @return The game tick delay, or -1 if none
     */
    public static int getTickDelay() {
        return tickDelay;
    }

    /**
     * Used for low cpu mode, the time to sleep between ticks
     *
     * @param tickDelay the new tick delay
     */
    public static void setTickDelay(int tickDelay) {
        Projection.tickDelay = tickDelay;
    }

    public static Polygon getTileShape(Positionable positionable) {
        FinePosition localLocation = positionable.toFine();

        int plane = Scene.getFloorLevel();
        int halfTile = TILE_PIXEL_SIZE / 2;


        if (mode % 2 == 0) {
            halfTile = TILE_PIXEL_SIZE;
            ScreenPosition p1 = fineToScreen(localLocation.getX(), localLocation.getY(), plane);
            ScreenPosition p2 = fineToScreen(localLocation.getX() + halfTile, localLocation.getY(), plane);
            ScreenPosition p3 = fineToScreen(localLocation.getX() + halfTile, localLocation.getY() + halfTile, plane);
            ScreenPosition p4 = fineToScreen(localLocation.getX(), localLocation.getY() + halfTile, plane);

            if (p1 == null || p2 == null || p3 == null || p4 == null) {
                return null;
            }

            Polygon poly = new Polygon();
            poly.addPoint(p1.getX(), p1.getY());
            poly.addPoint(p2.getX(), p2.getY());
            poly.addPoint(p3.getX(), p3.getY());
            poly.addPoint(p4.getX(), p4.getY());

            return poly;
        } else {

            ScreenPosition p1 = fineToScreen(localLocation.getX() - halfTile, localLocation.getY() - halfTile, plane);
            ScreenPosition p2 = fineToScreen(localLocation.getX() - halfTile, localLocation.getY() + halfTile, plane);
            ScreenPosition p3 = fineToScreen(localLocation.getX() + halfTile, localLocation.getY() + halfTile, plane);
            ScreenPosition p4 = fineToScreen(localLocation.getX() + halfTile, localLocation.getY() - halfTile, plane);

            if (p1 == null || p2 == null || p3 == null || p4 == null) {
                return null;
            }

            Polygon poly = new Polygon();
            poly.addPoint(p1.getX(), p1.getY());
            poly.addPoint(p2.getX(), p2.getY());
            poly.addPoint(p3.getX(), p3.getY());
            poly.addPoint(p4.getX(), p4.getY());

            return poly;
        }
    }

    public static boolean isLowCPUMode() {
        return !landscapeRenderingEnabled && !modelRenderingEnabled && tickDelay > 0;
    }

    public static void setLowCPUMode(boolean lowCPUMode) {
        setLandscapeRenderingEnabled(!lowCPUMode);
        setModelRenderingEnabled(!lowCPUMode);
        setTickDelay(lowCPUMode ? 20 : 0); //cant set it too high, as it would affect scripts
        Game.getClient().setLowMemory(lowCPUMode);
    }

    public static Point toMinimap(Position position) {
        return toMinimap(position, false);
    }

    public static Point toMinimap(Position position, boolean ignoreLimit) {
        int angle = Game.getClient().getMapRotation() & 0x7FF;
        FinePosition absolute = position.toFine();
        FinePosition relative = Players.getLocal().getPosition().toFine();

        int x = (absolute.getX() >> 5) - (relative.getX() >> 5);
        int y = (absolute.getY() >> 5) - (relative.getY() >> 5);

        if (ignoreLimit || (x * x + y * y) < 6400) {
            int sin = SINE[angle];
            int cos = COSINE[angle];

            int xx = y * sin + cos * x >> 16;
            int yy = sin * x - y * cos >> 16;

            int minimapX = 765 - 208;

            x = (minimapX + 167 / 2) + xx;
            y = (167 / 2 - 1) + yy;
            return new Point(x, y);
        }

        return null;
    }
}
