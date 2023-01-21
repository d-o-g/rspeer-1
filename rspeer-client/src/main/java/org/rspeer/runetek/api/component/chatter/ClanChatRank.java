package org.rspeer.runetek.api.component.chatter;

/**
 * Created by Spencer on 27/01/2018.
 */
public enum ClanChatRank {

    UNRANKED((byte) -1),
    FRIEND((byte) 0),
    RECRUIT((byte) 1),
    CORPORAL((byte) 2),
    SERGEANT((byte) 3),
    LIEUTENANT((byte) 4),
    CAPTAIN((byte) 5),
    GENERAL((byte) 6),
    OWNER((byte) 7);

    private final byte value;

    ClanChatRank(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
