package org.rspeer.runetek.api.component.chatter;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.providers.RSChatter;
import org.rspeer.runetek.providers.RSIgnoredPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Spencer on 27/01/2018.
 */
public final class IgnoredPlayers {

    private IgnoredPlayers() {
        throw new IllegalAccessError();
    }

    /**
     * @param predicate The predicate to select the players
     * @return All ignored players matching the predicate
     */
    public static RSIgnoredPlayer[] getAll(Predicate<? super RSIgnoredPlayer> predicate) {
        List<RSIgnoredPlayer> members = new ArrayList<>();
        for (RSChatter member : Game.getClient().getIgnoredPlayers()) {
            if (member instanceof RSIgnoredPlayer && predicate.test(((RSIgnoredPlayer) member))) {
                members.add((RSIgnoredPlayer) member);
            }
        }
        return members.toArray(new RSIgnoredPlayer[0]);
    }

    /**
     * @return All ignored players matching
     */
    public static RSIgnoredPlayer[] getAll() {
        return getAll(Predicates.always());
    }

    /**
     * @return The number of people on your ignore list
     */
    public static int getCount() {
        return Game.getClient().getSocialSystem().getIgnoreListContext().getCount();
    }
}
