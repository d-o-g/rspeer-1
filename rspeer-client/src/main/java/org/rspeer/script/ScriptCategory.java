package org.rspeer.script;

/**
 * Created by Spencer on 03/04/2018.
 */
public enum ScriptCategory {

    COMBAT,
    PRAYER,
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
    CONSTRUCTION,
    MONEY_MAKING,
    MINIGAME,
    QUESTING,
    MAGIC,
    TOOL,
    OTHER;

    @Override
    public String toString() {
        String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
