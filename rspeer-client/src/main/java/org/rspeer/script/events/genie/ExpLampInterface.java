package org.rspeer.script.events.genie;

import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.providers.RSWorld;

import java.util.HashMap;
import java.util.Map;

public enum ExpLampInterface {

    PARENT("Parent", 134, "Continue"),
    TOP_TEXT("Top text", 2, "Choose the stat you wish to be advanced!"),
    ATTACK("Attack", 3, "Advance Attack"),
    STRENGTH("Strength", 4, "Advance Strength"),
    RANGED("Ranged", 5, "Advance Ranged"),
    MAGIC("Magic", 6, "Advance Magic"),
    DEFENCE("Defence", 7, "Advance Defence"),
    HITPOINTS("Hitpoints", 8, "Advance Hitpoints"),
    PRAYER("Prayer", 9, "Advance Prayer"),
    AGILITY("Agility", 10, "Advance Agility"),
    HERBLORE("Herblore", 11, "Advance Herblore"),
    THIEVING("Thieving", 12, "Advance Thieving"),
    CRAFTING("Crafting", 13, "Advance Crafting"),
    RUNECRAFTING("Runecrafting", 14, "Advance Runecraft"),
    MINING("Mining", 15, "Advance Mining"),
    SMITHING("Smithing", 16, "Advance Smithing"),
    FISHING("Fishing", 17, "Advance Fishing"),
    COOKING("Cooking", 18, "Advance Cooking"),
    FIREMAKING("Firemaking", 19, "Advance Firemaking"),
    WOODCUTTING("Woodcutting", 20, "Advance Woodcutting"),
    FLETCHING("Fletching", 21, "Advance Fletching"),
    SLAYER("Slayer", 22, "Advance Slayer"),
    FARMING("Farming", 23, "Advance Farming"),
    CONSTRUCTION("Construction", 24, "Advance Construction"),
    HUNTER("Hunter", 25, "Advance Hunter"),
    CONFIRM("Confirm", 26, "Continue");

    private static final Map<String, ExpLampInterface> lookup = new HashMap<>();

    //TODO add herblore after finding varp for druidic ritual completion
    private static final ExpLampInterface[] NON_CB_SKILLS = {
            AGILITY, THIEVING, CRAFTING, RUNECRAFTING, MINING, SMITHING, FISHING,
            COOKING, FIREMAKING, WOODCUTTING, FLETCHING, SLAYER, FARMING, CONSTRUCTION, HUNTER
    };

    private static final ExpLampInterface[] F2P_NON_CB_SKILLS = {
            CRAFTING, RUNECRAFTING, MINING, SMITHING, FISHING, COOKING, FIREMAKING, WOODCUTTING
    };

    static {
        for (ExpLampInterface i : ExpLampInterface.values()) {
            lookup.put(i.name, i);
        }
    }

    private String name;
    private int id;
    private String action;

    ExpLampInterface(String name, int id, String action) {
        this.name = name;
        this.id = id;
        this.action = action;
    }

    public static ExpLampInterface getSkill(String name) {
        return lookup.get(name);
    }

    public static ExpLampInterface[] getNonCombatSkills() {
        if (Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)) {
            return NON_CB_SKILLS;
        }
        return F2P_NON_CB_SKILLS;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }
}