package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.scene.Scene;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.*;

public abstract class Action {

    private static final Map<Integer, String> OPCODE_NAME_MAPPINGS = new LinkedHashMap<>();

    static {
        try {
            for (Field field : ActionOpcodes.class.getDeclaredFields()) {
                if (field.getType() == int.class) {
                    int value = field.getInt(null);
                    OPCODE_NAME_MAPPINGS.put(value, field.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final int opcode;
    protected final int primary;
    protected final int secondary, tertiary;

    protected Action(int opcode, int primary, int secondary, int tertiary) {
        this.opcode = opcode;
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    public static Action valueOf(int op, int primary, int secondary, int tertiary) {
        if (op >= 2000) {
            op -= 2000;
        }

        if (op >= ITEM_ON_NPC && op <= NPC_ACTION_4 || op == EXAMINE_NPC) {
            return new NpcAction(op, primary);
        } else if (op >= PLAYER_ACTION_0 && op <= PLAYER_ACTION_7 || op == ITEM_ON_PLAYER) {
            return new PlayerAction(op, primary);
        } else if ((op >= ITEM_ON_OBJECT && op <= OBJECT_ACTION_3)
                || op == EXAMINE_OBJECT || op == OBJECT_ACTION_4) {
            return new ObjectAction(op, primary, secondary, tertiary);
        } else if (op == CANCEL) {
            return new CancelAction();
        } else if (op == WALK_HERE) {
            //TODO inject coords in as secondary and tertiary somehow, since the game ignores them
            //and passes mouseX/mouseY
            return new WalkAction(secondary, tertiary, Scene.getFloorLevel());
        } else if (op >= ITEM_ON_PICKABLE && op <= PICKABLE_ACTION_4 || op == EXAMINE_PICKABLE) {
            return new PickableAction(op, primary, secondary, tertiary);
        }

        if (op == INTERFACE_ACTION || op == INTERFACE_ACTION_2 || op == SPELL_ON_COMPONENT) {
            return new IndexedComponentAction((primary + 1), secondary, tertiary);
        } else if (op >= BUTTON_INPUT && op <= BUTTON_DIALOG) {
            return new ButtonAction(op, secondary, tertiary);
        }

        return null;
    }

    public String getOpcodeName() {
        return OPCODE_NAME_MAPPINGS.getOrDefault(getOpcode(), "OPCODE_" + getOpcode());
    }

    public int getOpcode() {
        return opcode;
    }

    public int getPrimary() {
        return primary;
    }

    public int getSecondary() {
        return secondary;
    }

    public int getTertiary() {
        return tertiary;
    }

    @Override
    public String toString() {
        return String.format("%s<%s>", getClass().getSimpleName(), getOpcodeName());
    }
}
