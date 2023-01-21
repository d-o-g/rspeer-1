package org.rspeer.runetek.event.types;

import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.MenuActionListener;

public final class MenuActionEvent extends Event {

    private final int secondaryArg, tertiaryArg, opcode, primaryArg;
    private final String action, target;

    public MenuActionEvent(int secondaryArg, int tertiaryArg, int opcode, int primaryArg, String action, String target) {
        super("Static");
        this.secondaryArg = secondaryArg;
        this.tertiaryArg = tertiaryArg;
        this.opcode = opcode;
        this.primaryArg = primaryArg;
        this.action = action;
        this.target = target;
    }

    public int getSecondaryArg() {
        return secondaryArg;
    }

    public String getAction() {
        return action;
    }

    public String getTarget() {
        return target;
    }

    public int getTertiaryArg() {
        return tertiaryArg;
    }

    public int getPrimaryArg() {
        return primaryArg;
    }

    public int getOpcode() {
        return opcode;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof MenuActionListener) {
            ((MenuActionListener) listener).notify(this);
        }
    }

    @Override
    public String toString() {
        return ActionOpcodes.verbose(opcode, primaryArg, secondaryArg, tertiaryArg);
    }
}
