package org.rspeer.script.events;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptBlockingEvent;

public final class BankPin extends ScriptBlockingEvent {

    private static final int BANK_PIN_WIDGET_ROOT = 213;
    private static final int HEADER_PIN_FIRST_CHILD = 3;
    private static final int NUMBER_CHILD_START = 16;
    private static final int NUMBER_CHILD_END = 34;

    public BankPin(Script ctx) {
        super(ctx);
    }

    @Override
    public boolean validate() {
        GameAccount account = ctx.getAccount();
        return Interfaces.isOpen(BANK_PIN_WIDGET_ROOT)
                && account != null
                && account.getPin() != -1;
    }

    @Override
    public void process() {
        int pin = ctx.getAccount().getPin();
        int pos = getPos();
        int digit = (int) (pin / (Math.pow(10, pos))) % 10;
        if (pin < 10 && pos < 3
                || pin < 100 && pos < 2
                || pin < 1000 && pos < 1) {
            digit = 0;
        }
        InterfaceComponent[] w = getDigitInterfaces(digit);
        if (w != null && w[0].interact("Select")) {
            Time.sleep(150, 350);
        }
    }

    private int getPos() {
        for (int i = 0; i < 4; i++) {
            InterfaceComponent w = Interfaces.getComponent(BANK_PIN_WIDGET_ROOT, HEADER_PIN_FIRST_CHILD + i);
            if (w == null) {
                break;
            }
            if (w.getText().equals("?")) {
                return 3 - i;
            }
        }
        return -1;
    }

    private InterfaceComponent[] getDigitInterfaces(int digit) {
        for (int child = NUMBER_CHILD_START; child <= NUMBER_CHILD_END; child += 2) {
            InterfaceComponent i = Interfaces.getComponent(BANK_PIN_WIDGET_ROOT, child);
            if (i == null) {
                return null;
            }
            InterfaceComponent[] children = i.getComponents();
            if (children.length == 2 && children[1].getText().equals(String.valueOf(digit))) {
                return children;
            }
        }
        return null;
    }
}
