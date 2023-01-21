package org.rspeer.runetek.api.input.menu;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.input.Mouse;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for the in game menu. Provides functionality to retrieve the bounds of the info,
 * information pertaining to each menu action row and what entities are currently being
 * hovered over, as well as functionality to open/close the menu.
 *
 * We as players only see 2 of the menu components when playing the game - The menu actions
 * and the options/targets. Internally, there is a lot more data. Each menu action is paired
 * with an opcode, and 3 arguments. The opcode is used as an identifier to know which type of
 * action it is - See {@link ActionOpcodes}. The 3 arguments are used to identify the target.
 * Not all arguments are required to be used, in some cases an action requires 0 arguments,
 * sometimes 1 or 2 only. For entities on the scene graph, the other arguments generally contain
 * the entity id, along with its regional x and y position. For components, the other arguments
 * contain the component uid, subcomponent index and menu action index
 */
public final class ContextMenu {

    private static final Map<Integer, String> OPCODE_TO_NAME = new HashMap<>();

    static {
        for (Field field : ActionOpcodes.class.getDeclaredFields()) {
            if (field.getType() == int.class) {
                try {
                    int value = field.getInt(null);
                    OPCODE_TO_NAME.put(value, field.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getOpcodeName(int opcode) {
        return OPCODE_TO_NAME.getOrDefault(opcode, String.valueOf(opcode));
    }

    private ContextMenu() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        return Game.getClient().isMenuOpen();
    }

    public static int getX() {
        return Game.getClient().getMenuX();
    }

    public static int getY() {
        return Game.getClient().getMenuY();
    }

    public static int getWidth() {
        return Game.getClient().getMenuWidth();
    }

    public static int getHeight() {
        return Game.getClient().getMenuHeight();
    }

    public static Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public static Rectangle getBounds(int rowIndex) {
        return new Rectangle(getX(), getY() + 18 + rowIndex * 15 + 1, getWidth(), 15);
    }

    public static int getRowCount() {
        return Game.getClient().getMenuRowCount();
    }

    public static String[] getActions() {
        return Game.getClient().getMenuActions();
    }

    public static String[] getTargets() {
        return Game.getClient().getMenuTargets();
    }

    public static int[] getOpcodes() {
        return Game.getClient().getMenuOpcodes();
    }

    public static int[] getPrimaryArguments() {
        return Game.getClient().getMenuPrimaryArgs();
    }

    public static int[] getSecondaryArguments() {
        return Game.getClient().getMenuSecondaryArgs();
    }

    public static int[] getTertiaryArguments() {
        return Game.getClient().getMenuTertiaryArgs();
    }

    public static boolean[] getShiftClickActions() {
        return Game.getClient().getMenuShiftClickActions();
    }

    public static long[] getOnCursorUids() {
        return Game.getClient().getOnCursorUids();
    }

    public static int getOnCursorCount() {
        return Game.getClient().getOnCursorCount();
    }

    public static Rectangle getClosingBounds() {
        if (!isOpen()) {
            return null;
        }
        return new Rectangle(
                getX() - 10,
                getY() - 10,
                getWidth() + 20,
                getHeight() + 20
        );
    }

    public static boolean close() {
        if (!isOpen()) {
            return true;
        }
        Rectangle bounds = getClosingBounds();
        if (bounds == null) {
            return true;
        }
        int x = bounds.x + bounds.width < 503 ? Random.nextInt(bounds.x + bounds.width, 503) : Random.nextInt(1, bounds.x - 1);
        int y = bounds.y + bounds.height < 765 ? Random.nextInt(bounds.y + bounds.height, 765) : Random.nextInt(1, bounds.y - 1);
        Mouse.move(x, y);
        return !bounds.contains(Mouse.getX(), Mouse.getY()) && Time.sleepUntil(() -> !isOpen(), 800);
    }

    public static boolean open(boolean onViewport) {
        if (!isOpen()) {
            if (onViewport) { //for entities (aka not widgets) it is required to open menu on the viewport
                Mouse.move(200, 200); //TODO change this to random anywhere on viewport
            }
            Mouse.click(false, 200, 200);
            return Time.sleepUntil(ContextMenu::isOpen, 800);
        }
        return true;
    }

    public static boolean open() {
        return open(false);
    }

    public static long compileUid(int x, int y, int type, boolean uninteractive, int index) {
        long uid = (long) ((x & 0x7f) | (y & 0x7f) << 7 | (type & 3) << 14) | ((long) index & 0xffffffffL) << 17;
        if (uninteractive) {
            uid |= 0x10000L;
        }
        return uid;
    }

    public static long uidFor(Pickable o) {
        return compileUid(o.getSceneX(), o.getSceneY(), 3, false, 0);
    }

    public static long uidFor(PathingEntity o) {
        return compileUid(0, 0, 1, false, o.getIndex());
    }
}
