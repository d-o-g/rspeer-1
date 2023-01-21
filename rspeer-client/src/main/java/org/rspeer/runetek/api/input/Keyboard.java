package org.rspeer.runetek.api.input;

import org.rspeer.runetek.api.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by MadDev on 11/16/17.
 */
public final class Keyboard {

    private Keyboard() {
        throw new IllegalAccessError();
    }

    public static void sendText(String text) {
        for (char c : text.toCharArray()) {
            sendKey(c);
        }
    }

    public static void pressEnter() {
        pressEventKey(KeyEvent.VK_ENTER);
    }

    public static void sendKey(char key) {
        getCanvas().dispatchEvent(generateEvent(key, KeyEvent.KEY_TYPED));
    }

    public static void pressEventKey(int eventKey) {
        getCanvas().dispatchEvent(generateEvent(eventKey, KeyEvent.KEY_PRESSED));
        getCanvas().dispatchEvent(generateEvent(eventKey, KeyEvent.KEY_RELEASED));
    }

    private static KeyEvent generateEvent(char key, int event) {
        AWTKeyStroke stroke = AWTKeyStroke.getAWTKeyStroke(key);
        return new KeyEvent(getCanvas(),
                event,
                System.currentTimeMillis(), stroke.getModifiers(), stroke.getKeyCode(),
                stroke.getKeyChar());
    }

    private static KeyEvent generateEvent(int key, int event) {
        return new KeyEvent(getCanvas(), event, System.currentTimeMillis(), 0, key,
                (char) key, KeyEvent.KEY_LOCATION_STANDARD);
    }

    private static Canvas getCanvas() {
        return Game.getClient().getCanvas();
    }

}
