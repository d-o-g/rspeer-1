package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.component.chatter.ClanChatRank;

public interface RSClanMember extends RSAssociate {

    default ClanChatRank getRankType() {
        for (ClanChatRank rank : ClanChatRank.values()) {
            if (rank.getValue() == getRank()) {
                return rank;
            }
        }
        return ClanChatRank.UNRANKED;
    }
}