package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.query.PlayerQueryBuilder;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSPlayer;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class Players {

    /** Max number of players allowed in the region **/
    public static final int MAXIMUM_PLAYER_COUNT = 2048;

    private Players() {
        throw new IllegalAccessError();
    }

    /**
     * @return All of the players loaded in your region.
     */
    public static Player[] getLoaded() {
        return getLoaded(Predicates.always());
    }

    /**
     * @param predicate The predicate that filters the results.
     * @return All of the filtered players loaded in your region.
     */
    public static Player[] getLoaded(Predicate<? super Player> predicate) {
        RSClient client = Game.getClient();
        RSPlayer[] raw = client.getPlayers();
        List<Player> players = new ArrayList<>();
        for (RSPlayer provider : raw) {
            if (provider != null) {
                Player player = provider.getWrapper();
                if (predicate.test(player)) {
                    players.add(player);
                }
            }
        }
        return players.toArray(new Player[0]);
    }

    public static Player getAt(int index) {
        return Functions.mapOrDefault(() -> Game.getClient().getPlayer(index), RSPlayer::getWrapper, null);
    }

    /**
     * @return Your game character.
     */
    public static Player getLocal() {
        return Functions.wrapOrDefault(() -> Game.getClient().getPlayer(), RSPlayer::getWrapper, null);
    }

    /**
     * @param predicate The predicate to select the players
     * @return The nearest Player matching the given Predicate
     */
    public static Player getNearest(Predicate<? super Player> predicate) {
        Player nearest = null;
        double nearestDistance = 1000;
        for (Player entity : getLoaded(predicate)) {
            double dist = Distance.between(Players.getLocal(), entity);
            if (dist < nearestDistance) {
                nearestDistance = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    /**
     * @param names The names to search for
     * @return The nearest Player matching any of the given names
     */
    public static Player getNearest(String... names) {
        return getNearest(new NamePredicate<>(names));
    }

    public static Player[] getSorted(Comparator<? super Player> comparator,
                                  Predicate<? super Player> predicate) {
        Player[] players = getLoaded(predicate);
        Arrays.sort(players, comparator);
        return players;
    }

    public static Player getBest(Comparator<? super Player> comparator, Predicate<? super Player> predicate, Player default_) {
        Player[] players = getSorted(comparator, predicate);
        return players.length > 0 ? players[0] : default_;
    }

    public static Player getBest(Comparator<? super Player> comparator, Predicate<? super Player> predicate) {
        return getBest(comparator, predicate, null);
    }

    public static PlayerQueryBuilder newQuery() {
        return new PlayerQueryBuilder();
    }
}
