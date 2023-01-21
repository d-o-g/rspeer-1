package org.rspeer.runetek.api.movement.pathfinding;

import java.awt.*;

/**
 * Created by Zachary Herridge on 7/9/2018.
 */
@Deprecated
public class EdgeType {

    public static final int BASIC = 0;
    public static final int PLANE_CHANGE = 1;
    public static final int CUSTOM = 2;
    public static final int DOOR = 3;

    public static final Color[] DEBUG_COLORS = new Color[]{Color.BLACK, Color.ORANGE, Color.PINK, Color.GREEN};
}
