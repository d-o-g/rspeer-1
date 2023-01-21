package org.rspeer.runetek.api.commons;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSWorld;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public enum BankLocation {

    //TODO favour checks for farming and woodcutting guilds

    LUMBRIDGE_CASTLE(new Position(3208, 3220, 2)),
    //LUMBRIDGE_CELLAR(new Position(3218, 9622), () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)), //TODO get varp value
    FALADOR_WEST(new Position(2947, 3368)),
    FALADOR_EAST(new Position(3013, 3355)),
    VARROCK_WEST(new Position(3185, 3436)),
    VARROCK_EAST(new Position(3253, 3420)),
    ARDOUGNE_NORTH(new Position(2615, 3332)),
    ARDOUGNE_SOUTH(new Position(2655, 3283)),
    EDGEVILLE(new Position(3094, 3491)),
    DRAYNOR(new Position(3092, 3245)),
    AL_KHARID(new Position(3269, 3167)),
    SEERS_VILLAGE(new Position(2727, 3493)),
    CATHERBY(new Position(2809, 3441)),
    YANILLE(new Position(2613, 3094)),
    FISHING_GUILD(new Position(2586, 3419), () -> Skills.getCurrentLevel(Skill.FISHING) >= 68
            && Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    MINING_GUILD(new Position(3013, 9718), () -> Skills.getCurrentLevel(Skill.MINING) >= 60
            && Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    WOODCUTTING_GUILD(new Position(1588, 3477), () -> Skills.getCurrentLevel(Skill.WOODCUTTING) >= 60
            && Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    FARMING_GUILD(new Position(1253, 3741), () -> Skills.getCurrentLevel(Skill.FARMING) >= 45
            && Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    CANIFIS(new Position(3512, 3480)),
    GRAND_EXCHANGE("Grand Exchange booth", "Bank", new Position(3163, 3487), Type.BANK_BOOTH),
    LOVAKENGJ_HOUSE(new Position(1526, 3740)),
    HOSIDIUS_HOUSE(new Position(1748, 3599)),
    SHAYZIEN_HOUSE(new Position(1505, 3615)),
    ARCEUUS_HOUSE(new Position(1629, 3745)),
    PISCARILIUS_HOUSE(new Position(1805, 3788)),
    LLETYA(new Position(2353, 3165)),
    MOTHERLODE_MINE("Bank chest", "Use", new Position(3761, 5666), Type.BANK_CHEST),
    ZANARIS("Bank chest", "Use", new Position(2381, 4458), Type.BANK_CHEST, () -> Varps.get(147) == 6),
    KOUREND_CASTLE(new Position(1612, 3681, 2)),

    GNOME_STRONGHOLD(new Position(2444, 3424, 1),
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    GNOME_STRONGHOLD_WEST(new Position(2441, 3487, 1),
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    GNOME_STRONGHOLD_SOUTH(new Position(2448, 3481, 1),
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    CORSAIR_COVE(new Position(2569, 2865),
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)
                    && (Varps.get(1677) == 60 || Varps.getBitValue(6071) == 60)),
    SHILO_VILLAGE("Banker", "Bank", new Position(2853, 2955), Type.NPC,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers) && Varps.get(116) == 15),
    HOSIDIUS_VINERY("Bank chest", "Use", new Position(1809, 3565), Type.BANK_CHEST,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    HOSIDIUS_MESS("Bank chest", "Use", new Position(1675, 3615), Type.BANK_CHEST,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),

    PORT_KHAZARD("Bank chest", "Use", new Position(2661, 3163, 0), Type.BANK_CHEST),
    ZEAH_SHORE("Bank chest", "Use", new Position(1720, 3465), Type.BANK_CHEST),
    SHANTAY_PASS("Bank chest", "Use", new Position(3309, 3120), Type.BANK_CHEST),
    CASTLE_WARS("Bank chest", "Use", new Position(2443, 3083), Type.BANK_CHEST),
    WINTERTODT("Bank chest", "Bank", new Position(1639, 3943), Type.BANK_CHEST),
    BARBARIAN_ASSAULT("Bank chest", "Use", new Position(2536, 3573), Type.BANK_CHEST),
    BLAST_FURNACE("Bank chest", "Use", new Position(1948, 4956, 0), Type.BANK_CHEST),

    FEROX_ENCLAVE("Bank chest", "Use", new Position(3130, 3631, 0), Type.BANK_CHEST),

    @Deprecated
    CLAN_WARS("Bank chest", "Use", new Position(3130, 3631, 0), Type.BANK_CHEST),

    DUEL_ARENA("Open chest", "Bank", new Position(3382, 3267, 0), Type.BANK_CHEST),

    //pvp chests
    CAMELOT_PVP("Bank chest", "Use", new Position(2756, 3479, 0), Type.BANK_CHEST,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isPVP)),
    LUMBRIDGE_PVP("Bank chest", "Use", new Position(3221, 3217, 0), Type.BANK_CHEST,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isPVP)),
    EDGEVILLE_PVP("Bank chest", "Use", new Position(3093, 3469, 0), Type.BANK_CHEST,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isPVP)),
    FALADOR_PVP("Bank chest", "Use", new Position(2978, 3344, 0), Type.BANK_CHEST,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isPVP)),

    MISCELLANIA("Banker", "Bank", new Position(2618, 3895), Type.NPC),
    FIGHT_CAVES("TzHaar-Ket-Zuh", "Bank", new Position(2446, 5178), Type.NPC),
    BURTHORPE("Emerald Benedict", "Bank", new Position(3047, 4974, 1), Type.NPC),

    /**
     * There's a naming conflict with bank and deposit box location ,as a quick fix
     * I just added "_DB" to the deposit box constant name
     */
    SHILO_VILLAGE_DB("Bank deposit box", "Deposit", new Position(2852, 2951), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers) && Varps.get(116) == 15),
    MINING_GUILD_DB("Bank deposit box", "Deposit", new Position(3013, 9718), Type.DEPOSIT_BOX,
            () -> Skills.getCurrentLevel(Skill.MINING) >= 60 && Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    FARMING_GUILD_DB("Bank deposit box", "Deposit", new Position(1253, 3741), Type.DEPOSIT_BOX,
            () -> Skills.getCurrentLevel(Skill.FARMING) >= 45 && Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    CATHERBY_DB("Bank deposit box", "Deposit", new Position(2729, 3492), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    YANILLE_DB("Bank deposit box", "Deposit", new Position(2611, 3089), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    EDGEVILLE_DB("Bank deposit box", "Deposit", new Position(3098, 3498), Type.DEPOSIT_BOX),
    PORT_SARIM_DB("Bank deposit box", "Deposit", new Position(3045, 3235), Type.DEPOSIT_BOX),
    VARROCK_WEST_DB("Bank deposit box", "Deposit", new Position(3180, 3434), Type.DEPOSIT_BOX),
    FALADOR_EAST_DB("Bank deposit box", "Deposit", new Position(3018, 3357), Type.DEPOSIT_BOX),
    FALADOR_WEST_DB("Bank deposit box", "Deposit", new Position(2944, 3368), Type.DEPOSIT_BOX),
    SEERS_VILLAGE_DB("Bank deposit box", "Deposit", new Position(2807, 3439), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    ARDOUGNE_NORTH_DB("Bank deposit box", "Deposit", new Position(2613, 3333), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    ARDOUGNE_SOUTH_DB("Bank deposit box", "Deposit", new Position(2654, 3281), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    DRAYNOR_VILLAGE_DB("Bank deposit box", "Deposit", new Position(3094, 3241), Type.DEPOSIT_BOX),
    LUMBRIDGE_TOP_DB("Bank deposit box", "Deposit", new Position(3209, 3217, 2), Type.DEPOSIT_BOX),
    KHAZARD_DB("Bank deposit box", "Deposit", new Position(2664, 3159, 0), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers)),
    BLAST_FURNACE_DB("Bank deposit box", "Deposit", new Position(1950, 4956, 0), Type.DEPOSIT_BOX,
            () -> Functions.mapOrElse(Worlds::getLocal, RSWorld::isMembers));

    private final String name, action;
    private final Position position;
    private final Type type;
    private final BooleanSupplier condition;

    BankLocation(String name, String action, Position tile, Type type, BooleanSupplier condition) {
        this.name = name;
        this.action = action;
        this.position = tile;
        this.type = type;
        this.condition = condition;
    }

    BankLocation(String name, String action, Position tile, Type type) {
        this(name, action, tile, type, () -> true);
    }

    BankLocation(Position position, BooleanSupplier condition) {
        this("Bank booth", "Bank", position, Type.BANK_BOOTH, condition);
    }

    BankLocation(Position position) {
        this(position, () -> true);
    }

    public static BankLocation getNearestTo(Positionable to) {
        return getNearestTo(Predicates.always(), to);
    }

    public static BankLocation getNearestTo(Predicate<BankLocation> predicate, Positionable to) {
        double distance = Integer.MAX_VALUE;
        BankLocation closest = null;
        for (BankLocation bank : BankLocation.values()) {
            if (bank.condition.getAsBoolean() && predicate.test(bank) && bank.getPosition().distance(to) < distance) {
                closest = bank;
                distance = bank.getPosition().distance(to);
            }
        }
        return closest;
    }

    public static BankLocation getNearest(Predicate<BankLocation> predicate) {
        return getNearestTo(predicate, Players.getLocal());
    }

    public static BankLocation getNearestWithdrawable() {
        return getNearest(bank -> bank.getType() != Type.DEPOSIT_BOX);
    }

    public static BankLocation getNearestDepositBox() {
        return getNearest(bank -> bank.getType() == Type.DEPOSIT_BOX);
    }

    public static BankLocation getNearestChest() {
        return getNearest(bank -> bank.getType() == Type.BANK_CHEST);
    }

    public static BankLocation getNearestBooth() {
        return getNearest(bank -> bank.getType() == Type.BANK_BOOTH);
    }

    public static BankLocation getNearestNpc() {
        return getNearest(bank -> bank.getType() == Type.NPC);
    }

    public static BankLocation getNearest() {
        return getNearest(Predicates.always());
    }

    public Position getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    public enum Type {
        NPC, DEPOSIT_BOX, BANK_CHEST, BANK_BOOTH
    }
}
