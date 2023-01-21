package org.rspeer.runetek.api.movement.global.augmenting;

import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.movement.position.Position;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class DirectTeleportNode extends TeleportNode {

    private Magic.Book book;
    private Spell spell;

    private Pattern name;
    private Pattern action;

    private int tablet;

    private float cost = 25; //10 would work too but nobody wastes teleports when theyre that close

    public DirectTeleportNode(Position destination) {
        super(Type.DIRECT_TELEPORT, destination);
    }

    public DirectTeleportNode spell(Magic.Book book, Spell spell) {
        this.book = book;
        this.spell = spell;

        if (spell == Spell.Modern.HOME_TELEPORT
                || spell == Spell.Ancient.ANCIENT_HOME_TELEPORT
                || spell == Spell.Lunar.LUNAR_HOME_TELEPORT
                || spell == Spell.Necromancy.HOME_TELEPORT) {
            cost += 50;
        }

        return this;
    }

    public DirectTeleportNode tablet(int tablet) {
        this.tablet = tablet;
        return this;
    }

    public DirectTeleportNode item(String namePattern, String actionPattern) {
        this.name = Pattern.compile(namePattern);
        this.action = Pattern.compile(actionPattern);
        return this;
    }

    public Pattern getName() {
        return name;
    }

    public Pattern getAction() {
        return action;
    }

    public Spell getSpell() {
        return spell;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public void setCost(float cost) {

    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }

        if (name != null && (Inventory.contains(name) || Equipment.contains(name))) {
            return true;
        }

        if (Inventory.contains(tablet)) {
            return true;
        }

        if (book != null && Magic.getBook() != book) {
            return false;
        }

        return Magic.canCast(spell); //if this isn't reliable, use runes and staff
    }
}
