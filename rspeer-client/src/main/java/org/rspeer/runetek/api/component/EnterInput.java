package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.*;
import org.rspeer.runetek.api.commons.*;
import org.rspeer.runetek.api.input.*;

import java.awt.event.KeyEvent;

public final class EnterInput {

    private static final int INTERFACE_INDEX = InterfaceComposite.CHATBOX.getGroup();

    private static final InterfaceAddress COMPONENT_ADDRESS = new InterfaceAddress(() ->
            Interfaces.getFirst(INTERFACE_INDEX, x -> x.getType() == InterfaceComponent.TYPE_LABEL && x.getTextColor() == 128)
    );

    private EnterInput() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.lookup(COMPONENT_ADDRESS);
        return component != null && component.isVisible();
    }

    public static String getEntry() {
        if (!isOpen()) {
            return "";
        }
        InterfaceComponent component = Interfaces.lookup(COMPONENT_ADDRESS);
        return component == null ? "" : component.getText().replace("*", "");
    }

    public static boolean initiate(int entry) {
        return initiate(String.valueOf(entry));
    }

    public static boolean initiate(String entry) {
        if (!isOpen()) {
            return false;
        }
        InterfaceComponent component = Interfaces.lookup(COMPONENT_ADDRESS);
        if (component == null) {
            return false;
        }
        String inputText = component.getText().replace("*", "");
        if (!inputText.equals(entry) && inputText.trim().length() > 0) {
            for (int i = 0; i < inputText.length(); i++) {
                Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
            }
        }

        if (!inputText.equals(entry)) {
            Keyboard.sendText(String.valueOf(entry));
        }
        Keyboard.pressEnter();
        return Time.sleepUntil(() -> !isOpen(), 1200);
    }

    public enum Type {

        //Unknown: CS2063, CS750, CS550, CS110, CS108, CS109
        //109 is related to joining houses but not limited to it? has the Last name: xxx shit. idk what to name this 1

        JOIN_CHANNEL(10),
        ADD_BEFRIENDED_PLAYER(2),
        ADD_IGNORED_PLAYER(4),
        DELETE_IGNORED_PLAYER(5),
        DELETE_BEFRIENDED_PLAYER(3),
        MESSAGE_BEFRIENDED_PLAYER(6),
        SET_A_NAME(13), //"You must set a name before you can chat."
        BANK_ITEM_SEARCH(11),
        PUBLIC_CHAT_FILTER(548)
        ;

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
