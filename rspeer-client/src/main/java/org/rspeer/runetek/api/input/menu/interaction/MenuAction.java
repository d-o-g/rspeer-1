package org.rspeer.runetek.api.input.menu.interaction;

public class MenuAction {

    private String action;
    private String target;
    private int opcode;
    private int primaryArg;
    private int secondaryArg;
    private int tertiaryArg;

    public MenuAction(String action, String target, int opcode, int primaryArg, int secondaryArg, int tertiaryArg) {
        this.action = action;
        this.target = target;
        this.opcode = opcode;
        this.primaryArg = primaryArg;
        this.secondaryArg = secondaryArg;
        this.tertiaryArg = tertiaryArg;
    }

    public String getAction() {
        return action;
    }

    public String getTarget() {
        return target;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getPrimaryArg() {
        return primaryArg;
    }

    public int getSecondaryArg() {
        return secondaryArg;
    }

    public int getTertiaryArg() {
        return tertiaryArg;
    }

    /*
    public static boolean isAutomated(int a, int b) {
        return a == 200 && b == 200;
    }
*/

    @Override
    public String toString() {
        return action + " -> " + target + " [" + opcode + "] (" + primaryArg + ", " + secondaryArg + ", " + tertiaryArg + ")";
    }
}