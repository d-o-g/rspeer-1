package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Position;

public final class Minigames {

    private static final int GROUP = InterfaceComposite.MINIGAMES_TAB.getGroup();
    private static final int TELEPORT_COMPONENT = 26;

    private Minigames() {
        throw new IllegalAccessError();
    }

    //FIELD_64
    public static boolean teleport(Destination destination) {
        Game.getClient().fireScriptEvent(124, destination.getIndex());
        InterfaceComponent component = Interfaces.getComponent(GROUP, TELEPORT_COMPONENT);
        if (component == null || (component = component.getComponent(x -> !x.isExplicitlyHidden())) == null) {
            return false;
        }

        if (component.interact(x -> true) && destination.getOption() == -1) {
            return true;
        }

        return Time.sleepUntil(Dialog::isOpen, 1200) && Dialog.process(destination.getOption());
    }

    public enum Destination {

        BARBARIAN_ASSAULT(1, new Position(2519, 3573)),
        BLAST_FURNACE(2, new Position(2934, 10183)),
        BURTHORPE_GAMES_ROOM(3, new Position(2207, 4939)),
        CASTLE_WARS(4, new Position(2441, 3094)),
        CLAN_WARS(5, new Position(3367, 3163)),
        FISHING_TRAWLER(7, new Position(2660, 3157)),
        LAST_MAN_STANDING(9, new Position(3399, 3177)),
        NIGHTMARE_ZONE(10, new Position(2611, 3122)),
        PEST_CONTROL(11, new Position(2655, 2657)),
        RAT_PITS_ARDOUGNE(13, 0, new Position(2565, 3320)),
        RAT_PITS_VARROCK(13, 1, new Position(3261, 3406)),
        RAT_PITS_KELDAGRIM(13, 2, new Position(2914, 10195)),
        RAT_PITS_PORT_SARIM(13, 3, new Position(3020, 3226)),
        SHADES_OF_MORTTON(14, new Position(3500, 3300)),
        TITHE_FARM(17, new Position(1788, 3591)),
        TROUBLE_BREWING(18, new Position(3817, 3025)),
        TZHAAR_FIGHT_PIT(19, new Position(2404, 5181));

        private final int index;
        private final int option;
        private final Position position;

        Destination(int index, int option, Position position) {
            this.index = index;
            this.option = option;
            this.position = position;
        }

        Destination(int index, Position position) {
            this(index, -1, position);
        }

        /**
         * @return The index of the minigame teleport within the drop down menu in the tab
         */
        public int getIndex() {
            return index;
        }

        /**
         * @return The chat option to select if applicable, else -1
         */
        public int getOption() {
            return option;
        }

        /**
         * @return The Position that this teleport will take you to
         */
        public Position getPosition() {
            return position;
        }
    }
}
