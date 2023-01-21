package org.rspeer.runetek.api.movement.transportation;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;

public final class CharterShip {

    private static final int ROOT = InterfaceComposite.CHARTER_SHIP.getGroup();

    /**
     * Calculates the nearest charter ship location
     *
     * @return the nearest charter ship location
     */
    public static Destination getNearest() {
        Destination best = null;
        double minDist = Double.MAX_VALUE;
        for (Destination destination : Destination.values()) {
            double currDist = destination.getPosition().distance();
            if (best == null || currDist < minDist) {
                best = destination;
                minDist = currDist;
            }
        }
        return best;
    }

    /**
     * Checks if the chartering interface is open
     *
     * @return true if the interface is open
     */
    public static boolean isInterfaceOpen() {
        return Interfaces.validateComponent(ROOT, 0);
    }


    /**
     * Opens the interface by finding a charter member and pressing charter
     *
     * @return true if the interaction was successful
     */
    public static boolean open() {
        Npc member = Npcs.getNearest(e -> e.containsAction("Charter"));
        return member != null && member.interact("Charter");
    }

    /**
     * Charters to the desired destination
     *
     * @param destination the destination to charter to
     * @return true if the chartering was successful
     */
    public static boolean charter(Destination destination) {
        if (!isInterfaceOpen()) {
            return false;
        }

        InterfaceComponent component = Interfaces.getComponent(ROOT, destination.getIndex());
        if (component == null || !component.isVisible() || !component.interact(destination.getAction())) {
            return false;
        }
        return Time.sleepUntil(Dialog::canContinue, 1200)
                && Dialog.processContinue()
                && Time.sleepUntil(Dialog::isViewingChatOptions, 1200)
                && Dialog.process(0);
    }

    public enum Destination {

        PORT_TYRAS("Port Tyras", 2, new Position(2142, 3122)),
        PORT_PHASMATYS("Port Phasmatys", 5, new Position(3702, 3503)),
        CATHERBY("Catherby", 8, new Position(2792, 3414)),
        SHIPYARD("Shipyard", 11, new Position(3001, 3032)),
        MUSA_POINT("Musa Point", 14, new Position(2954, 3155)),
        BRIMHAVEN("Brimhaven", 17, new Position(2760, 3239)),
        PORT_KHAZARD("Port Khazard", 20, new Position(2674, 3144)),
        PORT_SARIM("Port Sarim", 23, new Position(3038, 3192)),
        MOS_LE_HARMLESS("Mos Le'Harmless", 26, new Position(3671, 2931)),
        CORSAIR_COVE("Corsair Cove", 32, new Position(2587, 2851));

        private final String action;
        private final int index;
        private final Position position;

        Destination(String action, int index, Position position) {
            this.action = action;
            this.index = index;
            this.position = position;
        }

        /**
         * @return action to be clicked to charter
         */
        public String getAction() {
            return action;
        }

        /**
         * @return component index in the charter interface
         */
        public int getIndex() {
            return index;
        }

        /**
         * @return game position of this destination
         */
        public Position getPosition() {
            return position;
        }
    }
}