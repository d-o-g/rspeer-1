package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.InterfaceAddress;

/**
 * @author Man16
 */
public interface Spell {

    Magic.Book getBook();

    /**
     * @return The level required to cast a spell
     */
    int getLevelRequired();

    /**
     * @return An InterfaceAddress object containing information
     * in which the spell component can be retrieved via {@code Interfaces.lookup(address)}
     */
    InterfaceAddress getAddress();

    /**
     * @return The base experience gained from casting this spell
     */
    double getBaseExperience();

    /**
     * @return The base max damage this spell can deal
     */
    int getBaseMaxHit();

    /**
     * @return The disabled material id for the spell component
     */
    int getDisabledMaterialId();

    default String getName() {
        return toString();
    }

    boolean isAutoCasted();

    public enum Modern implements Spell {

        HOME_TELEPORT(0, 0, 0, 0, 1),
        WIND_STRIKE(1, 1, 5.5, 2, 65, 3),
        CONFUSE(2, 3, 13.0, 0, 66),
        ENCHANT_CROSSBOW_BOLT(3, 4, 9.0, 0, 408),
        WATER_STRIKE(4, 5, 7.5, 4, 67, 5),
        LEVEL_1_ENCHANT(5, 7, 17.5, 0, 68),
        EARTH_STRIKE(6, 9, 9.5, 6, 69, 7),
        WEAKEN(7, 11, 21.0, 0, 70),
        FIRE_STRIKE(8, 13, 11.5, 8, 71, 9),
        BONES_TO_BANANAS(9, 15, 25.0, 0, 72),
        WIND_BOLT(10, 17, 13.5, 9, 73, 11),
        CURSE(11, 19, 29.0, 0, 74),
        BIND(12, 20, 30.0, 0, 369),
        LOW_LEVEL_ALCHEMY(13, 21, 31.0, 0, 75),
        WATER_BOLT(14, 23, 16.5D, 10, 76, 13),
        VARROCK_TELEPORT(15, 25, 35.0, 0, 77),
        LEVEL_2_ENCHANT(16, 27, 37.0, 0, 78),
        EARTH_BOLT(17, 29, 19.5, 11, 79, 15),
        LUMBRIDGE_TELEPORT(18, 31, 41.0, 0, 80),
        TELEKINETIC_GRAB(19, 33, 43.0, 0, 81),
        FIRE_BOLT(20, 35, 22.5, 12, 82, 17),
        FALADOR_TELEPORT(21, 37, 47.0, 0, 83),
        CRUMBLE_UNDEAD(22, 39, 24.5, 15, 84),
        TELEPORT_TO_HOUSE(23, 40, 30.0, 0, 405),
        WIND_BLAST(24, 41, 25.5, 13, 85, 19),
        SUPERHEAT_ITEM(25, 43, 53.0, 0, 86),
        CAMELOT_TELEPORT(26, 45, 55.5, 0, 87),
        WATER_BLAST(27, 47, 28.5, 14, 88, 21),
        LEVEL_3_ENCHANT(28, 49, 59.0, 0, 89),
        IBAN_BLAST(29, 50, 30.0, 25, 103),
        SNARE(30, 50, 60.0, 3, 370),
        MAGIC_DART(31, 50, 30.0, 15, 374),
        ARDOUGNE_TELEPORT(32, 51, 61.0, 0, 104),
        EARTH_BLAST(33, 53, 31.5, 15, 90, 23),
        HIGH_LEVEL_ALCHEMY(34, 55, 65.0, 0, 91),
        CHARGE_WATER_ORB(35, 56, 56.0, 0, 92),
        LEVEL_4_ENCHANT(36, 57, 67.0, 0, 93),
        WATCHTOWER_TELEPORT(37, 58, 68.0, 0, 105),
        FIRE_BLAST(38, 59, 34.5, 16, 94, 25),
        CHARGE_EARTH_ORB(39, 60, 70.0, 0, 95),
        BONES_TO_PEACHES(40, 60, 65.0, 0, 404),
        SARADOMIN_STRIKE(41, 60, 61.0, 20, 111),
        CLAWS_OF_GUTHIX(42, 60, 61.0, 20, 110),
        FLAMES_OF_ZAMORAK(43, 60, 61.0, 20, 109),
        TROLLHEIM_TELEPORT(44, 61, 68.0, 0, 373),
        WIND_WAVE(45, 62, 36.0, 17, 96),
        CHARGE_FIRE_ORB(46, 63, 73.0, 0, 97),
        APE_ATOLL_TELEPORT(47, 64, 74.0, 0, 407),
        WATER_WAVE(48, 65, 37.5, 18, 98),
        CHARGE_AIR_ORB(49, 66, 76.0, 0, 99),
        VULNERABILITY(50, 66, 76.0, 0, 106),
        LEVEL_5_ENCHANT(51, 68, 78.0, 0, 100),
        TELEPORT_TO_KOUREND(52, 69, 82.0, 0, 410),
        EARTH_WAVE(53, 70, 40.0, 19, 101),
        ENFEEBLE(54, 73, 83.0, 0, 107),
        TELEOTHER_LUMBRIDGE(55, 74, 84.0, 0, 399),
        FIRE_WAVE(56, 75, 42.5, 20, 102),
        ENTANGLE(57, 79, 89.0, 5, 371),
        STUN(58, 80, 90.0, 0, 108),
        CHARGE(59, 80, 180.0, 0, 372),
        WIND_SURGE(60, 81, 44.5, 21, 412),
        TELEOTHER_FALADOR(61, 82, 92.0, 0, 400),
        WATER_SURGE(62, 85, 46.5, 22, 413),
        TELE_BLOCK(63, 85, 80.0, 0, 402),
        TELE_TO_BOUNTY_TARGET(64, 85, 45.0, 0, 409),
        LEVEL_6_ENCHANT(65, 87, 97.0, 0, 403),
        TELEOTHER_CAMELOT(67, 90, 100.0, 0, 401),
        EARTH_SURGE(67, 90, 48.5, 23, 414),
        LEVEL_7_ENCHANT(68, 93, 110.0, 0, 411),
        FIRE_SURGE(69, 95, 50.5, 24, 411);

        private static final int INTERFACE_INDEX = 218;

        private final InterfaceAddress address;
        private final int levelRequired;
        private final double baseExperience;
        private final int baseMaxHit;
        private final int disabledMaterialId;
        private final int autocastVarpValue;

        Modern(int componentIndex, int levelRequired, double baseExperience, int baseMaxHit,
                int disabledMaterialId, int autocastVarpValue) {
            this.levelRequired = levelRequired;
            this.baseExperience = baseExperience;
            this.baseMaxHit = baseMaxHit;
            this.disabledMaterialId = disabledMaterialId;
            this.autocastVarpValue = autocastVarpValue;
            this.address = new InterfaceAddress(INTERFACE_INDEX, componentIndex + 5);
        }

        Modern(int componentIndex, int levelRequired, double baseExperience, int baseMaxHit, int disabledMaterialId) {
            this(componentIndex, levelRequired, baseExperience, baseMaxHit, disabledMaterialId, -1);
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }

        @Override
        public Magic.Book getBook() {
            return Magic.Book.MODERN;
        }

        @Override
        public int getLevelRequired() {
            return levelRequired;
        }

        @Override
        public double getBaseExperience() {
            return baseExperience;
        }

        @Override
        public int getBaseMaxHit() {
            return baseMaxHit;
        }

        @Override
        public int getDisabledMaterialId() {
            return disabledMaterialId;
        }

        @Override
        public boolean isAutoCasted() {
            return Varps.get(108) == autocastVarpValue;
        }

        @Override
        public String toString() {
            String name = super.toString();
            return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
        }
    }

    /**
     * Created by Jasper Ketelaar
     *
     * Enum containing all the necessary information to interact with any of the spells in the lunar spellbook
     */
    public enum Lunar implements Spell {

        LUNAR_HOME_TELEPORT(0, 0, 0, -1, new InterfaceAddress(218, 99)),
        BAKE_PIE(65, 60, 0, 593, new InterfaceAddress(218, 100)),
        GEOMANCY(65, 60, 0, 613, new InterfaceAddress(218, 140)),
        CURE_PLANT(66, 60, 0, 617, new InterfaceAddress(218, 101)),
        MONSTER_EXAMINE(66, 61, 0, 627, new InterfaceAddress(218, 102)),
        NPC_CONTACT(67, 63, 0, 618, new InterfaceAddress(218, 103)),
        CURE_OTHER(68, 65, 0, 609, new InterfaceAddress(218, 104)),
        HUMIDIFY(68, 65, 0, 628, new InterfaceAddress(218, 105)),
        MOONCLAN_TELEPORT(69, 66, 0, 594, new InterfaceAddress(218, 106)),
        TELE_GROUP_MOONCLAN(70, 67, 0, 619, new InterfaceAddress(218, 107)),
        CURE_ME(71, 69, 0, 612, new InterfaceAddress(218, 108)),
        OURANIA_TELEPORT(71, 69, 0, 636, new InterfaceAddress(218, 142)),
        HUNTER_KIT(71, 70, 0, 629, new InterfaceAddress(218, 109)),
        WATERBIRTH_TELEPORT(72, 71, 0, 595, new InterfaceAddress(218, 110)),
        TELE_GROUP_WATERBIRTH(73, 72, 0, 620, new InterfaceAddress(218, 111)),
        CURE_GROUP(74, 74, 0, 615, new InterfaceAddress(218, 112)),
        BARBARIAN_TELEPORT(75, 76, 0, 626, new InterfaceAddress(218, 113)),
        STAT_SPY(75, 76, 0, 597, new InterfaceAddress(218, 114)),
        TELE_GROUP_BARBARIAN(76, 77, 0, 621, new InterfaceAddress(218, 115)),
        SPIN_FLAX(76, 75, 0, 635, new InterfaceAddress(218, 141)),
        SUPERGLASS_MAKE(77, 78, 0, 598, new InterfaceAddress(218, 116)),
        TAN_LEATHER(78, 81, 0, 633, new InterfaceAddress(218, 117)),
        KHAZARD_TELEPORT(78, 80, 0, 599, new InterfaceAddress(218, 118)),
        TELE_GROUP_KHAZARD(79, 81, 0, 622, new InterfaceAddress(218, 119)),
        DREAM(79, 82, 0, 630, new InterfaceAddress(218, 120)),
        STRING_JEWELRY(80, 83, 0, 600, new InterfaceAddress(218, 121)),
        STAT_RESTORE_POT_SHARE(81, 84, 0, 604, new InterfaceAddress(218, 122)),
        MAGIC_IMBUE(82, 86, 0, 602, new InterfaceAddress(218, 123)),
        FERTILE_SOIL(83, 87, 0, 603, new InterfaceAddress(218, 124)),
        BOOST_POTION_SHARE(84, 88, 0, 601, new InterfaceAddress(218, 125)),
        FISHING_GUILD_TELEPORT(85, 89, 0, 605, new InterfaceAddress(218, 126)),
        TELEPORT_TO_BOUNTY_TARGET(85, 45, 0, 409, new InterfaceAddress(218, 68)),
        TELE_GROUP_FISHING_GUILD(86, 90, 0, 623, new InterfaceAddress(218, 127)),
        PLANK_MAKE(86, 90, 0, 631, new InterfaceAddress(218, 128)),
        CATHERBY_TELEPORT(87, 92, 0, 606, new InterfaceAddress(218, 129)),
        TELE_GROUP_CATHERBY(88, 93, 0, 624, new InterfaceAddress(218, 130)),
        RECHARGE_DRAGONSTONE(89, 97.5, 0, 634, new InterfaceAddress(218, 131)),
        ICE_PLATEAU_TELEPORT(89, 96, 0, 607, new InterfaceAddress(218, 132)),
        TELE_GROUP_ICE_PLATEAU(90, 99, 0, 625, new InterfaceAddress(218, 133)),
        ENERGY_TRANSFER(91, 100, 0, 608, new InterfaceAddress(218, 134)),
        HEAL_OTHER(92, 101, 0, 610, new InterfaceAddress(218, 135)),
        VENGEANCE_OTHER(93, 108, 0, 611, new InterfaceAddress(218, 136)),
        VENGEANCE(94, 112, 0, 614, new InterfaceAddress(218, 137)),
        HEAL_GROUP(95, 124, 0, 616, new InterfaceAddress(218, 138)),
        SPELLBOOK_SWAP(96, 130, 0, 632, new InterfaceAddress(218, 139));

        private final int levelRequired;
        private final double baseExperience;
        private final int baseMaxHit;
        private final int disabledMaterialId;
        private final InterfaceAddress address;

        Lunar(int levelRequired, double baseExperience, int baseMaxHit, int disabledMaterialId, InterfaceAddress address) {
            this.levelRequired = levelRequired;
            this.baseExperience = baseExperience;
            this.baseMaxHit = baseMaxHit;
            this.disabledMaterialId = disabledMaterialId;
            this.address = address.component(address.getComponent() + 1);
        }

        @Override
        public Magic.Book getBook() {
            return Magic.Book.LUNAR;
        }

        @Override
        public int getLevelRequired() {
            return levelRequired;
        }

        @Override
        public double getBaseExperience() {
            return baseExperience;
        }

        @Override
        public int getBaseMaxHit() {
            return baseMaxHit;
        }

        @Override
        public int getDisabledMaterialId() {
            return disabledMaterialId;
        }

        @Override
        public boolean isAutoCasted() {
            return false;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }

        @Override
        public String toString() {
            String name = super.toString();
            return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
        }
    }

    /**
     * Created by Swagzini
     *
     * Enum containing all the necessary information to interact with any of the spells in the Ancient spellbook
     */
    public enum Ancient implements Spell {

        ANCIENT_HOME_TELEPORT(0, 0, 0, -1, new InterfaceAddress(218, 98)),
        SMOKE_RUSH(50, 30, 14, 379, new InterfaceAddress(218, 82)),
        SHADOW_RUSH(52, 31, 15, 387, new InterfaceAddress(218, 86)),
        PADDEWWA_TELEPORT(54, 64, 0, 391, new InterfaceAddress(218, 90)),
        BLOOD_RUSH(56, 33, 16, 383, new InterfaceAddress(218, 78)),
        ICE_RUSH(58, 34, 17, 375, new InterfaceAddress(218, 74)),
        SENNTISTEN_TELEPORT(60, 70, 0, 392, new InterfaceAddress(218, 91)),
        SMOKE_BURST(62, 36, 18, 380, new InterfaceAddress(218, 84)),
        SHADOW_BURST(64, 37, 19, 388, new InterfaceAddress(218, 88)),
        KHARYRLL_TELEPORT(66, 76, 0, 393, new InterfaceAddress(218, 92)),
        BLOOD_BURST(68, 39, 21, 384, new InterfaceAddress(218, 80)),
        ICE_BURST(70, 40, 22, 376, new InterfaceAddress(218, 76)),
        LASSAR_TELEPORT(72, 82, 0, 394, new InterfaceAddress(218, 93)),
        SMOKE_BLITZ(74, 42, 23, 381, new InterfaceAddress(218, 83)),
        SHADOW_BLITZ(76, 43, 24, 389, new InterfaceAddress(218, 87)),
        DAREEYAK_TELEPORT(78, 88, 0, 395, new InterfaceAddress(218, 94)),
        BLOOD_BLITZ(80, 45, 25, 385, new InterfaceAddress(218, 79)),
        ICE_BLITZ(82, 46, 26, 377, new InterfaceAddress(218, 75)),
        CARRALLANGAR_TELEPORT(84, 94, 0, 396, new InterfaceAddress(218, 95)),
        TELEPORT_TO_BOUNTY_TARGET(85, 45, 0, 409, new InterfaceAddress(218, 68)),
        SMOKE_BARRAGE(86, 48, 27, 382, new InterfaceAddress(218, 85)),
        SHADOW_BARRAGE(88, 49, 28, 390, new InterfaceAddress(218, 89)),
        ANNAKARL_TELEPORT(90, 100, 0, 397, new InterfaceAddress(218, 96)),
        BLOOD_BARRAGE(92, 51, 29, 386, new InterfaceAddress(218, 81)),
        ICE_BARRAGE(94, 52, 30, 378, new InterfaceAddress(218, 77)),
        GHORROCK_TELEPORT(96, 106, 0, 398, new InterfaceAddress(218, 97));

        private final int levelRequired;
        private final double baseExperience;
        private final int baseMaxHit;
        private final int disabledMaterialId;
        private final InterfaceAddress address;

        Ancient(int levelRequired, double baseExperience, int baseMaxHit, int disabledMaterialId, InterfaceAddress address) {
            this.levelRequired = levelRequired;
            this.baseExperience = baseExperience;
            this.baseMaxHit = baseMaxHit;
            this.disabledMaterialId = disabledMaterialId;
            this.address = address.component(address.getComponent() + 1);
        }

        @Override
        public Magic.Book getBook() {
            return Magic.Book.ANCIENT;
        }

        @Override
        public int getLevelRequired() {
            return levelRequired;
        }

        @Override
        public double getBaseExperience() {
            return baseExperience;
        }

        @Override
        public int getBaseMaxHit() {
            return baseMaxHit;
        }

        @Override
        public int getDisabledMaterialId() {
            return disabledMaterialId;
        }

        @Override
        public boolean isAutoCasted() {
            throw new UnsupportedOperationException("RSPeer does not currently have the autocast data for ancient spells");
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }

        @Override
        public String toString() {
            String name = super.toString();
            return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
        }
    }

    public enum Necromancy implements Spell {

        HOME_TELEPORT(0, 0, 406, 143),
        REANIMATE_GOBLIN(3, 6, 1272, 144),
        LUMBRIDGE_GRAVEYARD_TELEPORT(6, 10, 1294, 145),
        REANIMATE_MONKEY(7, 14, 1289, 146),
        REANIMATE_IMP(12, 24, 1283, 147),
        REANIMATE_MINOTAUR(16, 32, 1284, 148),
        DRAYNOR_MANOR_TELEPORT(17, 16, 1295, 149),
        REANIMATE_SCORPION(19, 38, 1282, 150),
        REANIMATE_BEAR(21, 42, 1281, 151),
        REANIMATE_UNICORN(22, 44, 1285, 152),
        BATTLEFRONT_TELEPORT(23, 19, 1325, 178),
        REANIMATE_DOG(26, 52, 1293, 153),
        MIND_ALTAR_TELEPORT(28, 22, 1296, 154),
        REANIMATE_CHAOS_DRUID(30, 60, 1276, 155),
        RESPAWN_TELEPORT(34, 27, 1319, 156),
        REANIMATE_GIANT(37, 74, 1280, 157),
        SALVE_GRAVEYARD_TELEPORT(40, 30, 1320, 158),
        REANIMATE_OGRE(40, 80, 1279, 159),
        REANIMATE_ELF(43, 86, 1275, 160),
        REANIMATE_TROLL(46, 92, 1277, 161),
        FENKENSTRAIN_CASTLE_TELEPORT(46, 50, 1321, 162),
        REANIMATE_HORROR(52, 104, 1291, 163),
        REANIMATE_KALPHITE(57, 114, 1286, 164),
        WEST_ARDOUGNE_TELEPORT(61, 68, 1322, 165),
        REANIMATE_DAGANNOTH(62, 124, 1278, 166),
        REANIMATE_BLOODVELD(65, 130, 1292, 167),
        HARMONY_ISLAND_TELEPORT(65, 74, 1323, 168),
        REANIMATE_TZHAAR(69, 138, 1287, 169),
        CEMETERY_TELEPORT(71, 82, 1324, 170),
        REANIMATE_DEMON(72, 144, 1273, 171),
        REANIMATE_AVIANSIE(78, 156, 1288, 172),
        RESURRECT_CROPS(78, 90, 1327, 173),
        BARROWS_TELEPORT(83, 90, 1325, 174),
        REANIMATE_ABYSSAL_CREATURE(85, 170, 1290, 175),
        APE_ATOLL_TELEPORT(90, 100, 1326, 176),
        REANIMATE_DRAGON(93, 186, 1274, 177);

        private static final int SPELL_BOOK_INTERFACE = 218;

        private final int level;
        private final double baseExperience;
        private final int disabledMaterial;
        private final InterfaceAddress address;

        Necromancy(int level, double baseExperience, int disabledMaterial, int index) {
            this.level = level;
            this.baseExperience = baseExperience;
            this.disabledMaterial = disabledMaterial;
            this.address = new InterfaceAddress(SPELL_BOOK_INTERFACE, index + 1);
        }

        @Override
        public double getBaseExperience() {
            return baseExperience;
        }

        @Override
        public Magic.Book getBook() {
            return Magic.Book.NECROMANCY;
        }

        @Override
        public int getLevelRequired() {
            return level;
        }

        @Override
        public int getBaseMaxHit() {
            return 0;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }

        @Override
        public int getDisabledMaterialId() {
            return disabledMaterial;
        }

        @Override
        public boolean isAutoCasted() {
            return false;
        }

        @Override
        public String toString() {
            String name = super.toString();
            return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
        }
    }
}
