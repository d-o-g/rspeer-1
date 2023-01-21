package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.input.Keyboard;

import java.awt.event.KeyEvent;

/**
 * @see EnterInput
 */
@Deprecated
public final class EnterAmount {

    private static final int INTERFACE_INDEX = InterfaceComposite.CHATBOX.getGroup();

    private static final InterfaceAddress COMPONENT_ADDRESS = new InterfaceAddress(() ->
            Interfaces.getFirst(INTERFACE_INDEX, x -> x.getType() == InterfaceComponent.TYPE_LABEL && x.getTextColor() == 128)
    );

    private EnterAmount() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.lookup(COMPONENT_ADDRESS);
        return component != null && component.isVisible();
    }

    public static boolean initiate(int amount) {
        if (!isOpen()) {
            return false;
        }
        InterfaceComponent component = Interfaces.lookup(COMPONENT_ADDRESS);
        if (component == null) {
            return false;
        }
        String inputText = component.getText().replace("*", "");
        String amountAsString = String.valueOf(amount);
        if (!inputText.equals(amountAsString) && inputText.trim().length() > 0) {
            for (int i = 0; i < inputText.length(); i++) {
                Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
            }
        }

        if (!inputText.equals(amountAsString)) {
            Keyboard.sendText(String.valueOf(amount));
        }
        Keyboard.pressEnter();
        return Time.sleepUntil(() -> !isOpen(), 1200);
    }
}
