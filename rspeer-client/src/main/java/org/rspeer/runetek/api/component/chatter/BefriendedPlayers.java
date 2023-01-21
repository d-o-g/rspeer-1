package org.rspeer.runetek.api.component.chatter;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.providers.RSBefriendedPlayer;
import org.rspeer.runetek.providers.RSChatter;
import org.rspeer.runetek.providers.RSNamePair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Spencer on 27/01/2018.
 */
public final class BefriendedPlayers {

    private BefriendedPlayers() {
        throw new IllegalAccessError();
    }

    /**
     * @param predicate The predicate to select the players
     * @return All befriended players matching the predicate
     */
    public static RSBefriendedPlayer[] getAll(Predicate<? super RSBefriendedPlayer> predicate) {
        List<RSBefriendedPlayer> members = new ArrayList<>();
        for (RSChatter member : Game.getClient().getBefriendedPlayers()) {
            if (member instanceof RSBefriendedPlayer && predicate.test(((RSBefriendedPlayer) member))) {
                members.add((RSBefriendedPlayer) member);
            }
        }
        return members.toArray(new RSBefriendedPlayer[0]);
    }

    /**
     * @return All befriended players
     */
    public static RSBefriendedPlayer[] getAll() {
        return getAll(Predicates.always());
    }

    /**
     * @return All befriended players in the current world
     */
    public static RSBefriendedPlayer[] getInWorld() {
        return getAll(x -> x.getWorld() == Worlds.getCurrent());
    }

    public static RSBefriendedPlayer getFirst(Predicate<? super RSBefriendedPlayer> predicate) {
        for (RSChatter member : Game.getClient().getBefriendedPlayers()) {
            if (member instanceof RSBefriendedPlayer && predicate.test(((RSBefriendedPlayer) member))) {
                return (RSBefriendedPlayer) member;
            }
        }
        return null;
    }

    public static RSBefriendedPlayer getFirst(String... names) {
        Predicate<RSBefriendedPlayer> predicate = new NamePredicate<>(names);
        RSBefriendedPlayer player = getFirst(predicate);
        if (player != null) {
            return player;
        }

        for (RSBefriendedPlayer bf : getAll()) {
            for (String name : names) {
                if (name.equalsIgnoreCase(bf.getLastName())) {
                    return bf;
                }
            }
        }
        return null;
    }

    /**
     * @return The number of people on your friends list
     */
    public static int getCount() {
        return Game.getClient().getSocialSystem().getFriendListContext().getCount();
    }
}
