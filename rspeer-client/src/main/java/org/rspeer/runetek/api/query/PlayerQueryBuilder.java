package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.commons.predicate.ActionPredicate;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;
import org.rspeer.runetek.api.scene.Players;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class PlayerQueryBuilder extends PathingEntityQueryBuilder<Player, PlayerQueryBuilder> {

    private final Supplier<List<? extends Player>> provider;


    private int[] equipmentIds = null;
    private int[] ids = null;
    private int[] prayers = null;
    private int[] skulls = null;
    private int[] teams = null;

    private String[] actions = null;

    public PlayerQueryBuilder(Supplier<List<? extends Player>> provider) {
        this.provider = provider;
    }

    public PlayerQueryBuilder() {
        this(() -> Arrays.asList(Players.getLoaded()));
    }

    @Override
    public Supplier<List<? extends Player>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected PositionableQueryResults<Player> createQueryResults(Collection<? extends Player> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public PlayerQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public PlayerQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public PlayerQueryBuilder overheads(int... prayers) {
        this.prayers = prayers;
        return self();
    }

    public PlayerQueryBuilder skulls(int... skulls) {
        this.skulls = skulls;
        return self();
    }

    public PlayerQueryBuilder teams(int... teams) {
        this.teams = teams;
        return self();
    }

    public PlayerQueryBuilder appearance(int... equipmentIds) {
        this.equipmentIds = equipmentIds;
        return self();
    }

    @Override
    public boolean test(Player player) {
        if (ids != null && !new IdPredicate<>(ids).test(player)) {
            return false;
        }

        if (actions != null && !new ActionPredicate<>(actions).test(player)) {
            return false;
        }

        if (prayers != null && !ArrayUtils.contains(prayers, player.getPrayerIcon())) {
            return false;
        }

        if (skulls != null && !ArrayUtils.contains(skulls, player.getSkullIcon())) {
            return false;
        }

        if (teams != null && !ArrayUtils.contains(teams, player.getTeam())) {
            return false;
        }

        if (equipmentIds != null && !player.getAppearance().isEquipped(equipmentIds)) {
            return false;
        }

        return super.test(player);
    }
}
