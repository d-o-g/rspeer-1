package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.adapter.scene.Projectile;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.NodeDeque;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.query.ProjectileQueryBuilder;
import org.rspeer.runetek.providers.RSNode;
import org.rspeer.runetek.providers.RSNodeDeque;
import org.rspeer.runetek.providers.RSProjectile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Zachary Herridge on 7/12/2018.
 */
public final class Projectiles {

    private Projectiles() {
        throw new IllegalAccessError();
    }

    public static Projectile[] getLoaded(Predicate<? super Projectile> predicate) {
        List<Projectile> loaded = new ArrayList<>();

        RSNodeDeque nodeDequePeer = Game.getClient().getProjectileDeque();
        if (nodeDequePeer == null || nodeDequePeer.getTail() == null
                || nodeDequePeer.getTail().getNext() == null) {
            return new Projectile[0];
        }

        NodeDeque nodeDeque = new NodeDeque(nodeDequePeer);
        for (RSNode next : nodeDeque) {
            if (next instanceof RSProjectile) {
                RSProjectile peer = (RSProjectile) next;
                Projectile wrappedProjectile = peer.getWrapper();
                if (predicate.test(wrappedProjectile)) {
                    loaded.add(wrappedProjectile);
                }
            }
        }
        return loaded.toArray(new Projectile[0]);
    }

    public static Projectile[] getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static Projectile getNearest(int... id) {
        return getNearest(new IdPredicate<>(id));
    }

    public static Projectile getNearest(Predicate<? super Projectile> predicate) {
        Projectile closest = null;
        double min = Double.MAX_VALUE;
        for (Projectile entity : getLoaded(predicate)) {
            double dist = Distance.between(Players.getLocal(), entity);
            if (dist < min) {
                min = dist;
                closest = entity;
            }
        }
        return closest;
    }

    public static Projectile[] getTargeting(PathingEntity entity) {
        return getLoaded(x -> x.getTarget() == entity);
    }

    public static Projectile getFirstTargeting(PathingEntity entity) {
        return getNearest(x -> x.getTarget() == entity);
    }

    public static Projectile[] getSorted(Comparator<? super Projectile> comparator, Predicate<? super Projectile> predicate) {
        Projectile[] projectiles = getLoaded(predicate);
        Arrays.sort(projectiles, comparator);
        return projectiles;
    }

    public static Projectile getBest(Comparator<? super Projectile> comparator, Predicate<? super Projectile> predicate, Projectile default_) {
        Projectile[] projectiles = getSorted(comparator, predicate);
        return projectiles.length > 0 ? projectiles[0] : default_;
    }

    public static Projectile getBest(Comparator<? super Projectile> comparator, Predicate<? super Projectile> predicate) {
        return getBest(comparator, predicate, null);
    }

    public static ProjectileQueryBuilder newQuery() {
        return new ProjectileQueryBuilder();
    }
}
