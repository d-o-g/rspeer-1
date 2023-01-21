package org.rspeer.runetek.api.component;

/**
 * Created by Spencer on 28/01/2018.
 */
public enum InterfaceComposite {

    INVENTORY(149),
    CLAN_CHAT(7),
    FRIENDS_LIST(429),
    ACCOUNT_MANAGEMENT(109),
    IGNORE_LIST(432),
    MINIMAP(160),
    CHATBOX(162),
    PRIVATE_CHAT(163),
    LOGOUT_TAB(182),
    AUTOCAST(201),
    EMOTES(216),
    SPELLBOOK(218),
    OPTIONS_TAB(261),
    SKILLS_TAB(320),
    QUESTS_TAB(399),
    MINIGAMES_TAB(76),
    ACHIEVEMENT_DIARY(259),
    KOUREND_FAVOUR_TAB(245),
    PRAYERS(541),
    COMBAT_TAB(593),
    AUDIO_TAB(239),
    WORN_EQUIPMENT(387),
    WORLD_SELECT(69),

    EQUIPMENT_STATS(84),
    VIEW_GUIDE_PRICES(464),
    ITEMS_KEPT_ON_DEATH(4),
    ADVANCED_INTERFACE_OPTIONS(60),
    NOTIFICATIONS(492),
    KEYBINDING(121),
    HOUSE_OPTIONS(370),
    BONDS(65),
    QUEST_JOURNAL(275),
    KOUREND_FAVOUR_OVERVIEW(243),
    QUICK_PRAYERS(77),
    EXP_DROP_CONFIGURATION(137),
    EXP_DROP(122),
    SKILL_GUIDE(214),
    CREATION(270),
    JEWELLERY_CREATION(446),

    WELCOME_SCREEN(378),
    WELCOME_SCREEN_2(50),
    WELCOME_SCREEN_3(413),

    FIXED_MODE_VIEWPORT(548),
    RESIZED_VIEWPORT_STONE(161),
    RESIZED_VIEWPORT_LINE(164),

    BANK(12),
    BANK_INVENTORY(15),
    TRADE_INVENTORY(336),
    TRADE(335),
    TRADE_SECOND(334),
    DEPOSIT_BOX(192),
    SHOP(300),
    SHOP_INVENTORY(301),

    GRAND_EXCHANGE(465),
    GRAND_EXCHANGE_INVENTORY(467),
    GRAND_EXCHANGE_HISTORY(383),
    GRAND_EXCHANGE_SETS(451),
    COLLECTION_BOX(402),

    POLL_BOOTH_OVERVIEW(310),
    POLL_BOOTH_RESULTS(345),

    ITEM_DIALOG(193),
    NPC_DIALOG(231),
    PLAYER_DIALOG(217),
    CHAT_OPTIONS(219),
    PLAIN_DIALOG(229),
    LEVEL_UP_DIALOG(233),
    GUIDANCE_DIALOG(11),

    CLOTHES_MAKEOVER(591),

    PEST_CONTROL(408),

    SLAYER_REWARDS(426),

    VOLCANIC_MINE(611),

    BARBARIAN_ASSAULT_ATTACK(485),
    BARBARIAN_ASSAULT_COLLECT(486),
    BARBARIAN_ASSAULT_DEFEND(487),
    BARBARIAN_ASSAULT_HEAL(488),

    QUEST_COMPLETED(277),

    CLUE_SCROLL_COMPLETED(73),

    BARROWS_REWARD(155),

    MOTHERLODE_MINE(382),

    PARTY_ROOM_CHEST(265),
    PARTY_ROOM_INVENTORY(266),

    FAIRY_RING(398),
    FAIRY_RING_LOG(381),
    CHARTER_SHIP(72),
    SPIRIT_TREE(187),
    GNOME_GLIDER(138),

    NIGHTMARE_ZONE(202);

    private final int group;

    InterfaceComposite(int group) {
        this.group = group;
    }

    public static InterfaceComposite getByGroup(int group) {
        for (InterfaceComposite composite : InterfaceComposite.values()) {
            if (composite.group == group) {
                return composite;
            }
        }
        return null;
    }

    public int getGroup() {
        return group;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }
}
