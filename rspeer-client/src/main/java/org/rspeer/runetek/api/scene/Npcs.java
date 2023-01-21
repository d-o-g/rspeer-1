package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.query.NpcQueryBuilder;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSNpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class Npcs {

    /**
     * Maximum number of Npcs allowed in the region
     **/
    public static final int MAXIMUM_NPC_COUNT = 32768;

    private Npcs() {
        throw new IllegalAccessError();
    }

    /**
     * @return All of the npcs loaded in your region.
     */
    public static Npc[] getLoaded() {
        return getLoaded(Predicates.always());
    }

    /**
     * @param predicate The predicate that filters the results.
     * @return All of the filtered npcs loaded in your region.
     */
    public static Npc[] getLoaded(Predicate<? super Npc> predicate) {
        RSClient client = Game.getClient();
        RSNpc[] raw = client.getNpcs();
        List<Npc> npcs = new ArrayList<>();
        for (RSNpc provider : raw) {
            if (provider != null) {
                Npc npc = provider.getWrapper();
                if (predicate.test(npc)) {
                    npcs.add(npc);
                }
            }
        }
        return npcs.toArray(new Npc[0]);
    }

    public static Npc getFirst(Predicate<? super Npc> predicate) {
        RSClient client = Game.getClient();
        RSNpc[] raw = client.getNpcs();
        for (RSNpc provider : raw) {
            if (provider != null) {
                Npc npc = provider.getWrapper();
                if (predicate.test(npc)) {
                    return npc;
                }
            }
        }
        return null;
    }

    /**
     * @param index The index of the npc defined by the client
     * @return An Npc at the given index, or null if none present
     */
    public static Npc getAt(int index) {
        return Functions.mapOrDefault(() -> Game.getClient().getNpc(index), RSNpc::getWrapper, null);
    }

    /**
     * @param predicate The predicate to select the npc
     * @return The nearest Npc matching the given Predicate
     */
    public static Npc getNearest(Predicate<? super Npc> predicate) {
        Npc nearest = null;
        double nearestDistance = 1000;
        for (Npc entity : getLoaded(predicate)) {
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
     * @return The nearest Npc matching the given names
     */
    public static Npc getNearest(String... names) {
        return getNearest(new NamePredicate<>(names));
    }

    /**
     * @param ids The ids to search for
     * @return The nearest Npc matching the given ids
     */
    public static Npc getNearest(int... ids) {
        return getNearest(new IdPredicate<>(ids));
    }

    public static Npc[] getSorted(Comparator<? super Npc> comparator,
            Predicate<? super Npc> predicate) {
        Npc[] npcs = getLoaded(predicate);
        Arrays.sort(npcs, comparator);
        return npcs;
    }

    public static Npc getBest(Comparator<? super Npc> comparator, Predicate<? super Npc> predicate, Npc default_) {
        Npc[] npcs = getSorted(comparator, predicate);
        return npcs.length > 0 ? npcs[0] : default_;
    }

    public static Npc getBest(Comparator<? super Npc> comparator, Predicate<? super Npc> predicate) {
        return getBest(comparator, predicate, null);
    }

    public static NpcQueryBuilder newQuery() {
        return new NpcQueryBuilder();
    }
}
