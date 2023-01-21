package org.rspeer.runetek.api.input;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.providers.subclass.GameCanvas;

import java.awt.*;
import java.awt.event.MouseEvent;

@Deprecated
public final class Mouse {

    public static final int CURSOR_STATE_DEFAULT = 0;
    public static final int CURSOR_STATE_YELLOW = 1;
    public static final int CURSOR_STATE_RED = 2;

    private Mouse() {
        throw new IllegalAccessError();
    }

    private static void dispatch(int id, long when, int modifiers,
                                 int x, int y, int clickCount, boolean popupTrigger, int button) {
        GameCanvas canvas = (GameCanvas) Game.getClient().getCanvas();
        MouseEvent evt = new MouseEvent(canvas, id, when, modifiers, x, y, clickCount, popupTrigger, button);
        evt.setSource("bot");
        canvas.dispatchEvent(evt);
    }

    public static int getX() {
        return Game.getClient().getMouseX();
    }

    public static int getY() {
        return Game.getClient().getMouseY();
    }

    @Deprecated
    public static void move(int x, int y) {
        if (x == -1 || y == -1 || getX() == x || getY() == y) {
            return;
        }
        dispatch(MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false, MouseEvent.NOBUTTON);
    }

    private static void pressMouse(boolean left, int x, int y) {
        dispatch(MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0,
                x, y, 1, true, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
    }

    private static void releaseMouse(boolean left, int x, int y) {
        dispatch(MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0,
                x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
        dispatch(MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
                x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
    }

    @Deprecated
    public static void click(boolean left, int delay, int x, int y) {
        move(x, y);
        Time.sleep(75, 100);
        pressMouse(left, x, y);
        Time.sleep(delay);
        releaseMouse(left, x, y);
    }

    @Deprecated
    public static void click(boolean left, int x, int y) {
        click(left, Random.nextInt(40, 80), x, y);
    }

    @Deprecated
    public static void click(int x, int y) {
        click(true, x, y);
    }

    @Deprecated
    public static void click(boolean left, int delay) {
        click(left, delay, getX(), getY());
    }

    @Deprecated
    public static void click(boolean left) {
        click(left, Random.nextInt(70, 120));
    }

    @Deprecated
    public static void click() {
        click(true);
    }

    public static int getCursorState() {
        return Game.getClient().getCursorState();
    }

    @Deprecated
    public static void move(Rectangle bounds) {
        Point p = Random.nextPoint(bounds);
        move(p.x, p.y);
    }
}
