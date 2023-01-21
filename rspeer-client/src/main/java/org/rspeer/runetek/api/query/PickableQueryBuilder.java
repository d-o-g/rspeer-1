package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.commons.Range;
import org.rspeer.runetek.api.commons.predicate.ActionPredicate;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;
import org.rspeer.runetek.api.scene.Pickables;

import java.util.*;
import java.util.function.Supplier;

public final class PickableQueryBuilder extends PositionableQueryBuilder<Pickable, PickableQueryBuilder> {

    private final Supplier<List<? extends Pickable>> provider;


    private Boolean stackable = null;
    private Boolean noted = null;

    private Range amount = null;

    private int[] ids = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] actions = null;

    public PickableQueryBuilder(Supplier<List<? extends Pickable>> provider) {
        this.provider = provider;
    }

    public PickableQueryBuilder() {
        this(() -> Arrays.asList(Pickables.getLoaded()));
    }

    @Override
    public Supplier<List<? extends Pickable>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected PositionableQueryResults<Pickable> createQueryResults(Collection<? extends Pickable> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public PickableQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public PickableQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public PickableQueryBuilder nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public PickableQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public PickableQueryBuilder stackable() {
        stackable = true;
        return self();
    }

    public PickableQueryBuilder nonstackable() {
        stackable = false;
        return self();
    }

    public PickableQueryBuilder noted() {
        noted = true;
        return self();
    }

    public PickableQueryBuilder unnoted() {
        noted = false;
        return self();
    }

    public PickableQueryBuilder amount(int minInclusive) {
        return amount(minInclusive, Integer.MAX_VALUE);
    }

    public PickableQueryBuilder amount(int minInclusive, int maxInclusive) {
        amount = Range.of(minInclusive, maxInclusive);
        return self();
    }

    public PickableQueryBuilder on(Position... positions) {
        provider(() -> {
            List<Pickable> pickables = new ArrayList<>();
            for (Position position : positions) {
                Collections.addAll(pickables, Pickables.getAt(position));
            }
            return pickables;
        });
        return super.on(positions);
    }

    @Override
    public boolean test(Pickable item) {
        if (ids != null && !new IdPredicate<>(ids).test(item)) {
            return false;
        }

        if (names != null && !new NamePredicate<>(names).test(item)) {
            return false;
        }

        if (nameContains != null && !new NamePredicate<>(true, nameContains).test(item)) {
            return false;
        }

        if (stackable != null && stackable != item.isStackable()) {
            return false;
        }

        if (noted != null && noted != item.isNoted()) {
            return false;
        }

        if (amount != null && !amount.within(item.getStackSize())) {
            return false;
        }

        if (actions != null && !new ActionPredicate<>(actions).test(item)) {
            return false;
        }

        return super.test(item);
    }
}
