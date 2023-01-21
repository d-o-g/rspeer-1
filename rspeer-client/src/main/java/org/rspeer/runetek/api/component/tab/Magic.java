package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;

/**
 * @author Man16
 * @author qverkk
 */
public final class Magic {

    @Deprecated
    public static final int SPELLBOOK_MODERN = 0;

    @Deprecated
    public static final int SPELLBOOK_ANCIENT = 1;

    @Deprecated
    public static final int SPELLBOOK_LUNAR = 2;

    @Deprecated
    public static final int SPELLBOOK_NECROMANCY = 3;

    private Magic() {
        throw new IllegalAccessError();
    }

    public static boolean canCast(Spell spell) {
        if (getBook() != spell.getBook()) {
            return false;
        }
        InterfaceComponent component = Interfaces.lookup(spell.getAddress());
        return component != null && component.getMaterialId() != spell.getDisabledMaterialId();
    }

    public static boolean interact(Spell spell, String action) {
        InterfaceComponent component = Interfaces.lookup(spell.getAddress());
        if (component == null) {
            return false;
        }

        if (component.getX() == 0) {
            Tabs.open(Tab.MAGIC);
            return false;
        }

        return component.interact(action);
    }

    public static boolean cast(Spell spell) {
        return interact(spell, "Cast");
    }

    public static boolean isSpellSelected() {
        return Game.getClient().isSpellSelected();
    }

    /**
     * @see Autocast#isUsable() ()
     * @deprecated
     */
    @Deprecated
    public static boolean canAutoCast() {
        return Varps.get(843) != 0;
    }

    /**
     * @see Autocast#isEnabled()
     * @deprecated
     */
    @Deprecated
    public static boolean isAutoCasting() {
        return Varps.get(108) != 0;
    }

    /**
     * @see Autocast#getMode()
     * @deprecated
     */
    @Deprecated
    public static boolean isDefensiveCasting() {
        return Varps.get(439) >>> 8 == 1;
    }

    public static Book getBook() {
        Book[] books = Book.values();
        int index = Varps.getBitValue(4070);
        if (index < 0 || index > 3) {
            throw new IllegalStateException("Unknown spellbook (" + index + ")! Please report this to a developer");
        }
        return books[index];
    }

    @Deprecated
    public static int getSpellBook() {
        int[] books = {SPELLBOOK_MODERN, SPELLBOOK_ANCIENT, SPELLBOOK_LUNAR, SPELLBOOK_NECROMANCY};
        int index = Varps.getBitValue(4070);
        if (index < 0 || index > 3) {
            throw new IllegalStateException("Unknown spellbook (" + index + ")! Please report this to a developer");
        }
        return books[index];
    }

    /**
     * Casts a spell on an interactable. This method does not run in one loop to allow the user to
     * specify whichever sleep they want between the select and the cast.
     *
     * @param spell        The spell to cast
     * @param interactable The interactable to cast the spell on
     * @return true if the spell was casted on the interactable.
     */
    public static boolean cast(Spell spell, Interactable interactable) {
        if (!isSpellSelected()) {
            Magic.cast(spell);
            return false;
        }
        return interactable.interact("Cast");
    }


    public enum Book {
        MODERN,
        ANCIENT,
        LUNAR,
        NECROMANCY
    }

    public static final class Autocast {

        private static final int AUTOCAST_VARP = 108;
        private static final int USABILITY_VARP = 843;
        private static final int AUTOCAST_MODE_VARP = 439;

        private static final InterfaceAddress AUTO_CAST_MENU_BASE = new InterfaceAddress(
                () -> Interfaces.getComponent(201, 1)
        );

        public static boolean isUsable() {
            return Varps.get(USABILITY_VARP) == 18;
        }

        public static boolean isEnabled() {
            return Varps.get(AUTOCAST_VARP) != 0;
        }

        public static Mode getMode() {
            return Varps.get(AUTOCAST_MODE_VARP) >>> 8 == 1 ? Mode.DEFENSIVE : Mode.OFFENSIVE;
        }

        public static Spell getSelectedSpell() {
            for (Spell spell : Spell.Modern.values()) {
                if (spell.isAutoCasted()) {
                    return spell;
                }
            }

            /*for (Spell spell : Spell.Ancient.values()) {
                if (spell.isAutoCasted()) {
                    return spell;
                }
            }*/
            return null;
        }

        public static boolean isSpellSelected(Spell spell) {
            return spell.isAutoCasted();
        }

        public static boolean select(Mode mode, Spell spell) {
            if (Skills.getCurrentLevel(Skill.MAGIC) < spell.getLevelRequired()) {
                return false;
            }
            return openAutoCastSettings(mode) && selectSpell(spell);
        }

        public static boolean isSelectionOpen() {
            return Interfaces.lookup(AUTO_CAST_MENU_BASE) != null;
        }

        private static boolean openAutoCastSettings(Mode mode) {
            if (isSelectionOpen()) {
                return true;
            }

            if (!openCombatTab()) {
                return false;
            }

            InterfaceComponent book = Interfaces.get(593, x -> x.containsAction("Choose spell"))[mode.ordinal()];
            return book != null
                    && book.interact("Choose spell")
                    && Time.sleepUntil(() -> Interfaces.lookup(AUTO_CAST_MENU_BASE) != null, 1200);
        }

        private static boolean selectSpell(Spell spell) {
            InterfaceComponent spellComponent = Interfaces.getFirst(201, 1,
                    x -> x.containsAction(y -> y.equalsIgnoreCase(spell.getName()))
            );

            if (spellComponent == null) {
                return false;
            }
            return spellComponent.interact(Predicates.always()) && Time.sleepUntil(spell::isAutoCasted, 1200);
        }

        private static boolean openCombatTab() {
            return Tabs.open(Tab.COMBAT) && Time.sleepUntil(() -> Tabs.isOpen(Tab.COMBAT), 1200);
        }

        public enum Mode {
            DEFENSIVE, OFFENSIVE;
        }
    }
}
