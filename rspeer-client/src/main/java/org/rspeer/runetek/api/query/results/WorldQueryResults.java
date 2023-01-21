package org.rspeer.runetek.api.query.results;

import org.rspeer.runetek.providers.RSWorld;

import java.util.Collection;
import java.util.Comparator;

public final class WorldQueryResults extends QueryResults<RSWorld, WorldQueryResults> {

    public WorldQueryResults(Collection<? extends RSWorld> results) {
        super(results);
    }

    /**
     * @return Sorts results by world id and then returns self
     */
    public WorldQueryResults indexed() {
        return sort(Comparator.comparingInt(RSWorld::getId));
    }

    /**
     * @return The world with the lowest id from the results
     */
    public RSWorld min() {
        return indexed().first();
    }

    /**
     * @return The world with the highest id from the results
     */
    public RSWorld max() {
        return indexed().last();
    }

    public WorldQueryResults sortByPopulation() {
        return sort(Comparator.comparingInt(RSWorld::getPopulation));
    }

    public RSWorld quietest() {
        return sortByPopulation().first();
    }

    public RSWorld busiest() {
        return sortByPopulation().last();
    }
}