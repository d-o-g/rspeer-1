package org.rspeer.runetek.api.query;

import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.query.results.WorldQueryResults;
import org.rspeer.runetek.providers.RSWorld;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class WorldQueryBuilder extends QueryBuilder<RSWorld, WorldQueryBuilder, WorldQueryResults> {

    private final Supplier<List<? extends RSWorld>> provider;

    private Boolean bounty = null;
    private Boolean current = null;
    private Boolean dmm = null;
    private Boolean highRisk = null;
    private Boolean lms = null;
    private Boolean members = null;
    private Boolean pvp = null;
    private Boolean sdmm = null;
    private Boolean skillTotal = null;
    private Boolean tournament = null;

    private int[] ids = null;

    private RSWorld.Locale[] locale = null;

    public WorldQueryBuilder(Supplier<List<? extends RSWorld>> provider) {
        this.provider = provider;
    }

    public WorldQueryBuilder() {
        this(() -> Arrays.asList(Worlds.getLoaded()));
    }

    @Override
    public Supplier<List<? extends RSWorld>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected WorldQueryResults createQueryResults(Collection<? extends RSWorld> raw) {
        return new WorldQueryResults(raw);
    }

    public WorldQueryBuilder bounty(boolean bounty) {
        this.bounty = bounty;
        return self();
    }

    public WorldQueryBuilder dmm(boolean dmm) {
        this.dmm = dmm;
        return self();
    }

    public WorldQueryBuilder highRisk(boolean highRisk) {
        this.highRisk = highRisk;
        return self();
    }

    public WorldQueryBuilder lms(boolean lms) {
        this.lms = lms;
        return self();
    }

    public WorldQueryBuilder members(boolean members) {
        this.members = members;
        return self();
    }

    public WorldQueryBuilder pvp(boolean pvp) {
        this.pvp = pvp;
        return self();
    }

    public WorldQueryBuilder sdmm(boolean sdmm) {
        this.sdmm = sdmm;
        return self();
    }

    public WorldQueryBuilder skillTotal(boolean skillTotal) {
        this.skillTotal = skillTotal;
        return self();
    }

    public WorldQueryBuilder tournament(boolean tournament) {
        this.tournament = tournament;
        return self();
    }

    public WorldQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public WorldQueryBuilder locale(RSWorld.Locale... locale) {
        this.locale = locale;
        return self();
    }

    public WorldQueryBuilder current(boolean current) {
        this.current = current;
        return self();
    }

    @Override
    public boolean test(RSWorld world) {
        if (current != null && (world.getId() == Worlds.getCurrent()) != current) {
            return false;
        }

        if (bounty != null && world.isBounty() != bounty) {
            return false;
        }

        if (dmm != null && world.isDeadman() != dmm) {
            return false;
        }

        if (highRisk != null && world.isHighRisk() != highRisk) {
            return false;
        }

        if (lms != null && world.isLastManStanding() != lms) {
            return false;
        }

        if (members != null && world.isMembers() != members) {
            return false;
        }

        if (pvp != null && world.isPVP() != pvp) {
            return false;
        }

        if (sdmm != null && world.isSeasonDeadman() != sdmm) {
            return false;
        }

        if (skillTotal != null && world.isSkillTotal() != skillTotal) {
            return false;
        }

        if (tournament != null && world.isTournament() != tournament) {
            return false;
        }

        if (ids != null && !ArrayUtils.contains(ids, world.getId())) {
            return false;
        }

        if (locale != null && !ArrayUtils.contains(locale, world.getLocale())) {
            return false;
        }

        return super.test(world);
    }
}
