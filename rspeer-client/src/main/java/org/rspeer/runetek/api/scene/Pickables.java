package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.NodeDeque;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.api.query.PickableQueryBuilder;
import org.rspeer.runetek.providers.RSNode;
import org.rspeer.runetek.providers.RSNodeDeque;
import org.rspeer.runetek.providers.RSPickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class Pickables {

    private Pickables() {
        throw new IllegalAccessError();
    }

    public static Pickable getFirstAt(int sceneX, int sceneY, int level) {
        Pickable[] pickables = getAt(sceneX, sceneY, level);
        return pickables.length > 0 ? pickables[0] : null;
    }

    public static Pickable getFirstAt(Position position) {
        ScenePosition pos = position.toScene();
        return getFirstAt(pos);
    }

    public static Pickable getFirstAt(ScenePosition position) {
        return getFirstAt(position.getX(), position.getY(), position.getFloorLevel());
    }

    public static Pickable[] getAt(Position position) {
		ScenePosition pos = position.toScene();
		return getAt(pos.getX(), pos.getY(), pos.getFloorLevel());
	}

    /**
     * @param sceneX The x position relative to the scene
     * @param sceneY The y position relative to the scene
     * @param level The floor level to search on
     * @return All pickables at the given location
     */
    public static Pickable[] getAt(int sceneX, int sceneY, int level) {
        if (sceneX >= 104 || sceneX < 0 || sceneY >= 104 || sceneY < 0 || level < 0 || level > 3) {
            return new Pickable[0];
        }

        RSNodeDeque<RSPickable> deque = Game.getClient().getPickableNodeDeques()[level][sceneX][sceneY];
        if (deque == null) {
            return new Pickable[0];
        }
        List<Pickable> items = new ArrayList<>();
        NodeDeque wrapped = new NodeDeque(deque);
        for (RSNode node : wrapped) {
            if (node instanceof RSPickable) {
                //items.add(((RSPickable) node).getWrapper());
                items.add(new Pickable((RSPickable) node, sceneX, sceneY, level));
            }
        }
        return items.toArray(new Pickable[0]);
    }

    /**
     * @param floorLevel The floor level to search on
     * @param predicate The predicate to filter the pickables
     * @return All pickables on the given floor level, matching the given predicate
     */
    public static Pickable[] getLoaded(int floorLevel, Predicate<? super Pickable> predicate) {
        List<Pickable> items = new ArrayList<>();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                for (Pickable obj : getAt(x, y, floorLevel)) {
                    if (predicate.test(obj)) {
                        items.add(obj);
                    }
                }
            }
        }
        return items.toArray(new Pickable[0]);
    }

    /**
     * @param predicate The predicate to filter the pickables
     * @return All pickables on the current floor level, matching the given predicate
     */
    public static Pickable[] getLoaded(Predicate<? super Pickable> predicate) {
        return getLoaded(Scene.getFloorLevel(), predicate);
    }

    public static Pickable[] getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static Pickable[] getLoaded(int floorLevel) {
        return getLoaded(floorLevel, Predicates.always());
    }

    /**
     * @param predicate The predicate to filter the pickables
     * @return The nearest pickable matching the given predicate
     */
    public static Pickable getNearest(Predicate<? super Pickable> predicate) {
        Pickable nearest = null;
        double nearestDistance = 1000;
        for (Pickable entity : getLoaded(predicate)) {
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
     * @return The nearest pickable matching any of the given names
     */
    public static Pickable getNearest(String... names) {
        return getNearest(new NamePredicate<>(names));
    }

    /**
     * @param ids The ids to search for
     * @return The nearest pickable matching any of the given ids
     */
    public static Pickable getNearest(int... ids) {
        return getNearest(new IdPredicate<>(ids));
    }

    public static Pickable[] getSorted(Comparator<? super Pickable> comparator,
                                     Predicate<? super Pickable> predicate) {
        Pickable[] pickables = getLoaded(predicate);
        Arrays.sort(pickables, comparator);
        return pickables;
    }

    public static Pickable getBest(Comparator<? super Pickable> comparator, Predicate<? super Pickable> predicate, Pickable default_) {
        Pickable[] pickables = getSorted(comparator, predicate);
        return pickables.length > 0 ? pickables[0] : default_;
    }

    public static Pickable getBest(Comparator<? super Pickable> comparator, Predicate<? super Pickable> predicate) {
        return getBest(comparator, predicate, null);
    }

    public static PickableQueryBuilder newQuery() {
        return new PickableQueryBuilder();
    }
}
