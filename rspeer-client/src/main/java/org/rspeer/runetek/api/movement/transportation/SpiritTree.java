package org.rspeer.runetek.api.movement.transportation;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;

public final class SpiritTree {

    private static final String DISABLED_COL = "<col=777777>";
    private static final InterfaceAddress SPIRIT_TREE_ADDRESS
            = new InterfaceAddress(InterfaceComposite.SPIRIT_TREE.getGroup(), 3);

    private SpiritTree() {
        throw new IllegalAccessError();
    }

    /**
     * Checks if tree interface is open
     *
     * @return true if tree interface is open
     */
    public static boolean isInterfaceOpen() {
        return Interfaces.validateComponent(SPIRIT_TREE_ADDRESS.getRoot(), SPIRIT_TREE_ADDRESS.getComponent()) && getInterface().isVisible();
    }

    /**
     * Opens the tree interface by finding a nearby spirit tree
     *
     * @return true if the interaction was successful
     */
    public static boolean open() {
        if (isInterfaceOpen()) {
            return true;
        }
        SceneObject tree = SceneObjects.getNearest("Spirit Tree");
        return tree != null && tree.interact("Travel");
    }

    /**
     * Travels to the given destination using the spirit tree interface
     *
     * @param destination the destination to travel to
     * @return true if the travel was succesful
     */
    public static boolean travel(Destination destination) {
        InterfaceComponent tree = getInterface();
        if (tree != null) {
            InterfaceComponent child = tree.getComponent(destination.getIndex());
            return child != null && child.isVisible()
                    && !child.getText().contains(DISABLED_COL) && child.interact("Continue");
        }
        return false;
    }

    private static InterfaceComponent getInterface() {
        return Interfaces.lookup(SPIRIT_TREE_ADDRESS);
    }

    public enum Destination {

        TREE_GNOME_VILLAGE(0, new Position(2542, 3170)),
        GNOME_STRONGHOLD(1, new Position(2461, 3444)),
        BATTLEFIELD_OF_KHAZARD(2, new Position(2555, 3259)),
        GRAND_EXCHANGE(3, new Position(3184, 3508)),
        FELDIP_HILLS(4, new Position(2488, 2850)),
        PORT_SARIM(5, new Position(3060, 3261)),
        ECTERIA(6, new Position(2163, 3855)),
        BRIMHAVEN(7, new Position(2802, 3201)),
        KOUREND(8, new Position(1693, 3539)),
        HOUSE(9, null);

        private final int index;
        private final Position position;

        Destination(int index, Position position) {
            this.index = index;
            this.position = position;
        }

        public int getIndex() {
            return index;
        }

        public Position getPosition() {
            return position;
        }
    }
}