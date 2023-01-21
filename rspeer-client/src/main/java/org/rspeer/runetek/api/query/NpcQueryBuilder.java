package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.predicate.ActionPredicate;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;
import org.rspeer.runetek.api.scene.Npcs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class NpcQueryBuilder extends PathingEntityQueryBuilder<Npc, NpcQueryBuilder> {

    private final Supplier<List<? extends Npc>> provider;


    private int[] ids = null;

    private String[] actions = null;

    public NpcQueryBuilder(Supplier<List<? extends Npc>> provider) {
        this.provider = provider;
    }

    public NpcQueryBuilder() {
        this(() -> Arrays.asList(Npcs.getLoaded()));
    }

    @Override
    public Supplier<List<? extends Npc>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected PositionableQueryResults<Npc> createQueryResults(Collection<? extends Npc> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public NpcQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public NpcQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    @Override
    public boolean test(Npc npc) {
        if (actions != null && !new ActionPredicate<>(actions).test(npc)) {
            return false;
        }

        if (ids != null && !new IdPredicate<>(ids).test(npc)) {
            return false;
        }

        return super.test(npc);
    }
}
