package org.rspeer.runetek.api.movement.pathfinding;

import java.awt.*;

@Deprecated
public class NodeType {

    public static final int BASIC = 0;
    public static final int CUSTOM = 1;
    public static final int TERMINATING = 2;
    public static final int DOOR = 3;
    public static final int STAIR = 4;

    public static final Color[] DEBUG_COLORS = new Color[]{Color.BLACK, Color.ORANGE, Color.PINK, Color.GREEN, Color.GREEN};

    public static boolean isSaveable(int type) {
        return type == STAIR || type == BASIC;
    }
}