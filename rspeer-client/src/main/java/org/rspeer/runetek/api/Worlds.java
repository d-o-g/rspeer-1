package org.rspeer.runetek.api;

import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.query.WorldQueryBuilder;
import org.rspeer.runetek.providers.RSWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Worlds {

    private Worlds() {
        throw new IllegalAccessError();
    }

    public static RSWorld[] getLoaded(Predicate<? super RSWorld> predicate) {
        return getLoadedOrLoad(predicate, 0);
    }

    private static RSWorld[] getLoadedOrLoad(Predicate<? super RSWorld> predicate, int attempt) {
        List<RSWorld> matched = new ArrayList<>();
        RSWorld[] worlds = Game.getClient().getWorlds();
        if (worlds == null || worlds.length < 5) {
            if(attempt > 10) {
                return new RSWorld[0];
            }
            Game.getClient().loadWorlds();
            return getLoadedOrLoad(predicate, attempt + 1);
        }

        for (RSWorld world : worlds) {
            if (world != null && predicate.test(world)) {
                matched.add(world);
            }
        }
        return matched.toArray(new RSWorld[0]);
    }

    public static RSWorld[] getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static RSWorld get(Predicate<? super RSWorld> predicate) {
        RSWorld[] worlds = getLoaded(predicate);
        return worlds.length > 0 ? worlds[0] : null;
    }

    public static RSWorld get(int id) {
        if (id < 300) {
            id += 300;
        }
        int world = id;
        return get(w -> w.getId() == world);
    }

    public static int getCurrent() {
        return Game.getClient().getCurrentWorld();
    }

    public static RSWorld getLocal() {
        return get(getCurrent());
    }

    public static WorldQueryBuilder newQuery() {
        return new WorldQueryBuilder();
    }
}
