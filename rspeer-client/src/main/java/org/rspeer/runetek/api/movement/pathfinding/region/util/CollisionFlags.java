package org.rspeer.runetek.api.movement.pathfinding.region.util;

import java.util.StringJoiner;

public class CollisionFlags {

    public static final int SOLID = 0x20000;
    public static final int BLOCKED = 0x200000;
    public static final int OCCUPIED = 0x100;
    public static final int UNINITIALIZED = 0x1000000;
    public static final int IMPENETRABLE = 0x40000000;

    public static final int WALL_NORTH = 0x2;
    public static final int WALL_EAST = 0x8;
    public static final int WALL_SOUTH = 0x20;
    public static final int WALL_WEST = 0x80;

    public static final int NORTH_WEST_WALL = 0x1;
    public static final int NORTH_EAST_WALL = 0x4;
    public static final int SOUTH_EAST_WALL = 0x10;
    public static final int SOUTH_WEST_WALL = 0x40;

    public static boolean isBlocked(int flag) {
        return check(flag, OCCUPIED | SOLID | BLOCKED | UNINITIALIZED);
    }

    public static boolean checkWalkable(Direction dir, int startFlag, int endFLag, boolean ignoreStartBlocked) {
        if (isBlocked(endFLag) || (!ignoreStartBlocked && isBlocked(startFlag))) {
            return false;
        }

        switch (dir) {
            case NORTH:
                if (check(startFlag, WALL_NORTH)) {
                    return false;
                }
                break;
            case SOUTH:
                if (check(startFlag, WALL_SOUTH)) {
                    return false;
                }
                break;
            case WEST:
                if (check(startFlag, WALL_WEST)) {
                    return false;
                }
                break;
            case EAST:
                if (check(startFlag, WALL_EAST)) {
                    return false;
                }
                break;
            case NORTH_EAST:
                if (check(startFlag, NORTH_EAST_WALL | WALL_NORTH | WALL_EAST) || (check(endFLag, WALL_SOUTH) && check(endFLag, WALL_WEST))) {
                    return false;
                }
                break;
            case NORTH_WEST:
                if (check(startFlag, NORTH_WEST_WALL | WALL_NORTH | WALL_WEST) || (check(endFLag, WALL_SOUTH) && check(endFLag, WALL_EAST))) {
                    return false;
                }
                break;
            case SOUTH_EAST:
                if (check(startFlag, SOUTH_EAST_WALL | WALL_SOUTH | WALL_EAST) || (check(endFLag, WALL_NORTH) && check(endFLag, WALL_WEST))) {
                    return false;
                }
                break;
            case SOUTH_WEST:
                if (check(startFlag, SOUTH_WEST_WALL | WALL_SOUTH | WALL_WEST) || (check(endFLag, WALL_NORTH) && check(endFLag, WALL_EAST))) {
                    return false;
                }
                break;
        }
        return true;
    }

    public static boolean check(int flag, int checkFlag) {
        return (flag & checkFlag) != 0;
    }

    public static String toString(int flag) {
        StringJoiner builder = new StringJoiner(", ");
        if (check(flag, OCCUPIED)) {
            builder.add("OCCUPIED");
        }
        if (check(flag, SOLID)) {
            builder.add("SOLID");
        }
        if (check(flag, BLOCKED)) {
            builder.add("BLOCKED");
        }
        if (check(flag, WALL_WEST)) {
            builder.add("WALL_WEST");
        }
        if (check(flag, SOUTH_WEST_WALL)) {
            builder.add("SOUTH_WEST_WALL");
        }
        if (check(flag, WALL_SOUTH)) {
            builder.add("WALL_SOUTH");
        }
        if (check(flag, SOUTH_EAST_WALL)) {
            builder.add("SOUTH_EAST_WALL");
        }
        if (check(flag, WALL_EAST)) {
            builder.add("WALL_EAST");
        }
        if (check(flag, NORTH_EAST_WALL)) {
            builder.add("NORTH_EAST_WALL");
        }
        if (check(flag, WALL_NORTH)) {
            builder.add("WALL_NORTH");
        }
        if (check(flag, NORTH_WEST_WALL)) {
            builder.add("NORTH_WEST_WALL");
        }
        if (check(flag, IMPENETRABLE)) {
            builder.add("IMPENETRABLE");
        }
        if (check(flag, UNINITIALIZED)) {
            builder.add("UNINITIALIZED");
        }
        return builder.toString();
    }
}