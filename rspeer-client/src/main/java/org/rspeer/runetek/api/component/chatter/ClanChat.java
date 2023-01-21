package org.rspeer.runetek.api.component.chatter;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.providers.RSChatter;
import org.rspeer.runetek.providers.RSClanMember;
import org.rspeer.runetek.providers.RSClanSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Spencer on 27/01/2018.
 */
public final class ClanChat {

    private static final int INTERFACE_INDEX = InterfaceComposite.CLAN_CHAT.getGroup();
    private static final int LEAVEJOIN_COMPONENT = 17;
    private static final int SETUP_COMPONENT = 19;

    private ClanChat() {
        throw new IllegalAccessError();
    }

    /**
     * @return The current channel name, or null if not in a channel
     */
    public static String getChannelName() {
        RSClanSystem sys = Game.getClient().getClanSystem();
        return sys != null && sys.getChannelName() != null ? sys.getChannelName() : "";
    }

    /**
     * @return The current channel owner, or null if not in a channel
     */
    public static String getChannelOwner() {
        RSClanSystem sys = Game.getClient().getClanSystem();
        return sys != null && sys.getChannelOwner() != null ? sys.getChannelOwner() : "";
    }

    /**
     * @return {@code true} if you are in a clan channel
     */
    public static boolean isInChannel() {
        RSClanSystem sys = Game.getClient().getClanSystem();
        return sys != null && sys.getChannelOwner() != null;
    }

    /**
     * @param predicate The predicate to select the members
     * @return All clan members accepted by the predicate
     */
    public static RSClanMember[] getMembers(Predicate<? super RSClanMember> predicate) {
        List<RSClanMember> members = new ArrayList<>();
        for (RSChatter member : Game.getClient().getClanMembers()) {
            if (member instanceof RSClanMember && predicate.test(((RSClanMember) member))) {
                members.add((RSClanMember) member);
            }
        }
        return members.toArray(new RSClanMember[0]);
    }

    /**
     * @return All clan members
     */
    public static RSClanMember[] getMembers() {
        return getMembers(Predicates.always());
    }

    /**
     * @return The {@code ClanChatRank} of the local player
     */
    public static ClanChatRank getLocalRank() {
        byte value = Game.getClient().getClanSystem().getLocalPlayerRank();
        for (ClanChatRank rank : ClanChatRank.values()) {
            if (rank.getValue() == value) {
                return rank;
            }
        }
        return ClanChatRank.UNRANKED;
    }

    /**
     * @return All clan members in your world
     */
    public static RSClanMember[] getInWorld() {
        return getMembers(x -> x.getWorld() == Worlds.getCurrent());
    }

    /**
     * @return The number of members in the current clan channel
     */
    public static int getMemberCount() {
        return getMembers().length;
    }
}
