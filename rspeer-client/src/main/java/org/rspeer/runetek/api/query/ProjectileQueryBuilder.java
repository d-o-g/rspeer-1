package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.adapter.scene.Projectile;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;
import org.rspeer.runetek.api.scene.Projectiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class ProjectileQueryBuilder extends PositionableQueryBuilder<Projectile, ProjectileQueryBuilder> {

    private final Supplier<List<? extends Projectile>> provider;


    private Boolean targeting = null;

    private int[] ids = null;

    private PathingEntity[] targets = null;

    public ProjectileQueryBuilder(Supplier<List<? extends Projectile>> provider) {
        this.provider = provider;
    }

    public ProjectileQueryBuilder() {
        this(() -> Arrays.asList(Projectiles.getLoaded()));
    }

    @Override
    public Supplier<List<? extends Projectile>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected PositionableQueryResults<Projectile> createQueryResults(Collection<? extends Projectile> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public ProjectileQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public ProjectileQueryBuilder targetless() {
        targeting = false;
        return self();
    }

    public ProjectileQueryBuilder targeting() {
        targeting = true;
        return self();
    }

    public ProjectileQueryBuilder targeting(PathingEntity... targets) {
        this.targets = targets;
        return self();
    }

    @Override
    public boolean test(Projectile projectile) {
        if (ids != null && !new IdPredicate<>(ids).test(projectile)) {
            return false;
        }

        if (targeting != null && targeting == (projectile.getTargetIndex() == -1)) {
            return false;
        }

        if (targets != null && !ArrayUtils.contains(targets, projectile.getTarget())) {
            return false;
        }

        return super.test(projectile);
    }
}
