package org.rspeer.runetek.event.types;

public enum ChatMessageType {

    SERVER(0),
    PUBLIC_MODERATOR(1),
    PUBLIC(2),
    PRIVATE_RECEIVED(3),

    ENGINE(4),

    @Deprecated
    TRADE_RECEIVED(4),
    PRIVATE_INFO(5),
    PRIVATE_SENT(6),
    PRIVATE_RECEIVED_MODERATOR(7),
    CLAN_CHANNEL(9),
    CLAN_CHANNEL_INFO(11),
    TRADE_SENT(12),
    BROADCAST(14),
    REPORT_ABUSE(26),
    EXAMINE_ITEM(27),
    EXAMINE_NPC(28),
    EXAMINE_OBJECT(29),
    FRIENDS_LIST_ADD(30),
    IGNORE_LIST_ADD(31),
    AUTOCHAT(90),
    AUTOCHAT_MODERATOR(91),
    GAME(99),
    TRADE(101),
    TRADE_INFO(102), //trade result
    DUEL(103),
    CLAN_CHALLENGE(104),
    FILTERED(105),
    TEN_SECOND_TIMEOUT(107),
    ACTION(109),
    UNKNOWN(-1);

    private final int id;

    ChatMessageType(int id) {
        this.id = id;
    }

    public static ChatMessageType lookup(int typeId) {
        for (ChatMessageType type : ChatMessageType.values()) {
            if (type.id == typeId) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }
}
