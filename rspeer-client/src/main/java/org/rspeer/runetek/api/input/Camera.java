package org.rspeer.runetek.api.input;

import org.rspeer.runetek.api.Game;

public final class Camera {

    private Camera() {
        throw new IllegalAccessError();
    }

    /**
     * @return The raw x position of the camera
     */
    public static int getX() {
        return Game.getClient().getCameraX();
    }

    /**
     * @return The raw y position of the camera
     */
    public static int getY() {
        return Game.getClient().getCameraY();
    }

    /**
     * @return The raw z position of the camera
     */
    public static int getZ() {
        return Game.getClient().getCameraZ();
    }

    /**
     * @return The current altitude, or pitch of the camera
     */
    public static int getPitch() {
        return Game.getClient().getCameraPitch();
    }

    /**
     * @return The current yaw of the camera
     */
    public static int getYaw() {
        return Game.getClient().getCameraYaw();
    }

    /**
     * @return The current camera angle
     */
    public static int getAngle() {
        return (int) ((360D / 2048) * Math.min(2047 - getYaw(), 2048));
    }

    public static boolean isLocked() {
        return Game.getClient().isCameraLocked();
    }
}