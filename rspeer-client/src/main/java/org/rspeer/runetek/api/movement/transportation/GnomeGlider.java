package org.rspeer.runetek.api.movement.transportation;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;

public final class GnomeGlider {

    private static final int GLIDER_INTERFACE = InterfaceComposite.GNOME_GLIDER.getGroup();

    private GnomeGlider() {
        throw new IllegalAccessError();
    }

    /**
     * Checks if gnome glider interface is opened
     *
     * @return true if the interface is opened
     */
    public static boolean isInterfaceOpen() {
        return Interfaces.getComponent(GLIDER_INTERFACE, 0) != null;
    }

    /**
     * Opens the gnome glider interface by interacting with a nearby glider
     *
     * @return true if the interaction was successful
     */
    public static boolean open() {
        if (isInterfaceOpen()) {
            return true;
        }
        Npc glider = Npcs.getNearest(e -> e.containsAction("Glider"));
        return glider != null && glider.interact("Glider");
    }


    /**
     * Travels to the specified destination using a nearby gnome glider
     *
     * @param destination the destination to travel to
     * @return true if the travel was successful
     */
    public static boolean travel(Destination destination) {
        if (!isInterfaceOpen()) {
            return false;
        }
        InterfaceComponent gliderComp = Interfaces.getComponent(GLIDER_INTERFACE, destination.getIndex());
        return gliderComp != null && gliderComp.isVisible() && gliderComp.interact(destination.getAction());
    }

    public enum Destination {

        GNOME_STRONGHOLD(4, "Ta Quir Priw", new Position(2465, 3501, 3)),
        WHITE_WOLF_MOUNTAIN(7, "Sindarpos", new Position(2850, 3497)),
        DIGSITE(10, "Lemanto Andra", new Position(3321, 3429), false),
        AL_KHARID(13, "Kar-Hewo", new Position(3284, 3213)),
        SHIPYARD(16, "Gandius", new Position(2970, 2972)),
        FELDIP_HILLS(21, "Lemantolly Undri", new Position(2545, 2972)),
        APE_ATOLL(25, "Ookookolly Undri", new Position(2711, 2802)),
        ;

        private final int index;
        private final String action;
        private final Position position;
        private final boolean canTravelFrom;

        Destination(int index, String action, Position position) {
            this(index, action, position, true);
        }

        Destination(int index, String action, Position position, boolean canTravelFrom) {
            this.index = index;
            this.action = action;
            this.position = position;
            this.canTravelFrom = canTravelFrom;
        }

        public int getIndex() {
            return index;
        }

        public String getAction() {
            return action;
        }

        public Position getPosition() {
            return position;
        }

        public boolean isCanTravelFrom() {
            return canTravelFrom;
        }
    }
}