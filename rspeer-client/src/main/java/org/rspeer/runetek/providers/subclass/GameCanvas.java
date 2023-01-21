package org.rspeer.runetek.providers.subclass;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Login;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Interfaces;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public abstract class GameCanvas extends Canvas {

    private static Consumer<Boolean> callback;
    private static boolean input = true;

    public static void setCallback(Consumer<Boolean> callback) {
        if (GameCanvas.callback != null) {
            throw new IllegalStateException("Already set!"); //for internal use only
        }
        GameCanvas.callback = callback;
    }

    public static boolean isInputEnabled() {
        return input;
    }

    public static void setInputEnabled(boolean inputEnabled) {
        GameCanvas.input = inputEnabled;
        if (callback != null) {
            callback.accept(inputEnabled);
        }
    }

    @Override
    public void processEvent(AWTEvent e) {
        if (!e.getSource().equals("bot") && e instanceof MouseEvent && !input) {
            Game.getEventMediator().mouseEvent((MouseEvent) e);
            return;
        }
        super.processEvent(e);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        super.addFocusListener(new FocusProxy(listener));
    }

    private class FocusProxy implements FocusListener {

        private final FocusListener original;

        private FocusProxy(FocusListener original) {
            this.original = original;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (!((Applet) Game.getClient()).hasFocus()) {
                original.focusGained(e);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            //do nothing, we never lose focus
        }

        /**
         * Dispatches a focusLost event to the component
         *
         * @param e The {@link java.awt.event.FocusEvent} to dispatch
         */
        public void loseFocus(FocusEvent e) {
            original.focusLost(e);
        }

        /**
         * Dispatches a focusLost event to the src component with the given event id
         *
         * @param src The source component
         * @param id  The id of the {@link java.awt.event.FocusEvent}
         */
        public void loseFocus(Component src, int id) {
            loseFocus(new FocusEvent(src, id));
        }

        /**
         * Dispatches a focusLost event to the component with the given event id
         *
         * @param id The id of the {@link java.awt.event.FocusEvent}
         */
        public void loseFocus(int id) {
            loseFocus(new FocusEvent(GameCanvas.this, id));
        }

        /**
         * Dispatches a focusGained event to the component
         *
         * @param e The {@link java.awt.event.FocusEvent} to dispatch
         */
        public void gainFocus(FocusEvent e) {
            original.focusGained(e);
        }

        /**
         * Dispatches a focusGained event to the src component with the given event id
         *
         * @param src The source component
         * @param id  The id of the {@link java.awt.event.FocusEvent}
         */
        public void gainFocus(Component src, int id) {
            gainFocus(new FocusEvent(src, id));
        }

        /**
         * Dispatches a focusGained event to the component with the given event id
         *
         * @param id The id of the {@link java.awt.event.FocusEvent}
         */
        public void gainFocus(int id) {
            gainFocus(new FocusEvent(GameCanvas.this, id));
        }
    }
}
