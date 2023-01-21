package org.rspeer.runetek.api.component.tab;

public enum Skill {

    ATTACK,
    DEFENCE,
    STRENGTH,
    HITPOINTS,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUTTING,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLORE,
    AGILITY,
    THIEVING,
    SLAYER,
    FARMING,
    RUNECRAFTING,
    HUNTER,
    CONSTRUCTION;

    public int getIndex() {
        return ordinal();
    }

    @Override
    public String toString() {
        String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}