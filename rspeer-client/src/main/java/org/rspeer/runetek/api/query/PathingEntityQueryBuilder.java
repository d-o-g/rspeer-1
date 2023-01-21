package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.commons.Range;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;

public abstract class PathingEntityQueryBuilder<T extends PathingEntity, Q extends QueryBuilder>
        extends RotatableQueryBuilder<T, Q> {

    private Boolean animating = null;
    private Boolean targeted = null;
    private Boolean targeting = null;
    private Boolean moving = null;
    private Boolean acceptPlayerTargeters = false;

    private Range health = null;

    private int[] animations = null;
    private int[] stances = null;

    private PathingEntity[] targeters = null;
    private PathingEntity[] targets = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] dialogues = null;

    public Q animating() {
        animating = true;
        return self();
    }

    public Q inanimate() {
        animating = false;
        return self();
    }

    public Q animations(int... animations) {
        this.animations = animations;
        return self();
    }

    public Q dialogues(String... dialogues) {
        this.dialogues = dialogues;
        return self();
    }

    public Q health(int minPercent, int maxPercent) {
        health = Range.of(minPercent, maxPercent);
        return self();
    }

    public Q health(int minPercent) {
        return health(minPercent, Integer.MAX_VALUE);
    }

    public Q names(String... names) {
        this.names = names;
        return self();
    }

    public Q nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public Q stances(int... stances) {
        this.stances = stances;
        return self();
    }

    public Q targeted(boolean targeted) {
        this.targeted = targeted;
        return self();
    }

    public Q targeted() {
        return targeted(true);
    }

    public Q acceptPlayerTargeters() {
        acceptPlayerTargeters = true;
        return self();
    }

    public Q targetless() {
        targeting = false;
        return self();
    }

    public Q targeting() {
        targeting = true;
        return self();
    }

    public Q targeters(PathingEntity... targeters) {
        this.targeters = targeters;
        return self();
    }

    public Q targeting(PathingEntity... targets) {
        this.targets = targets;
        return self();
    }

    public Q moving() {
        moving = true;
        return self();
    }

    public Q stationary() {
        moving = false;
        return self();
    }

    //TODO targetedBy

    @Override
    public boolean test(T e) {
        if (names != null && !new NamePredicate<>(names).test(e)) {
            return false;
        }

        if (nameContains != null && !new NamePredicate<>(true, nameContains).test(e)) {
            return false;
        }

        if (animating != null && animating != e.isAnimating()) {
            return false;
        }

        if (moving != null && moving != e.isMoving()) {
            return false;
        }

        if (targeting != null && targeting == (e.getTargetIndex() == -1)) {
            return false;
        }

        if (health != null && !health.within(e.getHealthPercent())) {
            return false;
        }

        if (animations != null && !ArrayUtils.contains(animations, e.getAnimation())) {
            return false;
        }

        if (dialogues != null && !ArrayUtils.contains(dialogues, e.getOverheadText())) {
            return false;
        }

        if (stances != null && !ArrayUtils.contains(stances, e.getStance())) {
            return false;
        }

        if (targets != null && !ArrayUtils.contains(targets, e.getTarget())) {
            return false;
        }

        if (targeters != null) {
            boolean match = false;
            for (PathingEntity targeter : targeters) {
                if (targeter.getTarget() == e) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                return false;
            }
        }

        if (targeted != null && hasTargeters(e) != targeted) {
            return false;
        }

        return super.test(e);
    }

    private boolean hasTargeters(PathingEntity e) {
        for (Npc npc : Npcs.getLoaded()) {
            if (npc.getTarget() == e) {
                return true;
            }
        }

        if (acceptPlayerTargeters) {
            for (Player player : Players.getLoaded()) {
                if (player.getTarget() == e) {
                    return true;
                }
            }
        }

        return false;
    }
}
