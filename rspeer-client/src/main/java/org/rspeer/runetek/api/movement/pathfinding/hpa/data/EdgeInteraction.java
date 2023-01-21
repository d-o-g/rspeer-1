package org.rspeer.runetek.api.movement.pathfinding.hpa.data;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class EdgeInteraction {

    public static final int NPC = 1;
    public static final int SCENE_ENTITY = 2;
    public static final int SPELL = 3;
    public static final int INTERFACE = 4;
    public static final int FAIRY_RING = 5;
    public static final int CHARTER = 6;

    private int type;
    private Map<String, Object> interactionData = new HashMap<>();

    public int getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return interactionData;
    }
}
