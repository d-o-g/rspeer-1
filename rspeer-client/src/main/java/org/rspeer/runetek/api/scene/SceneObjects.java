package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.api.query.SceneObjectQueryBuilder;
import org.rspeer.runetek.providers.RSSceneGraph;
import org.rspeer.runetek.providers.RSSceneObject;
import org.rspeer.runetek.providers.RSTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class SceneObjects {

    private SceneObjects() {
        throw new IllegalAccessError();
    }

    public static SceneObject getFirstAt(int sceneX, int sceneY, int level) {
        if (sceneX > 103 || sceneY > 103) {
            //people misunderstand/misuse this waaay too much
            throw new ArrayIndexOutOfBoundsException("This method only accepts coordinates relative to the scene. Perhaps you mean to use getFirstAt(Position) instead");
        }
        SceneObject[] objs = getAt(sceneX, sceneY, level);
        return objs.length > 0 ? objs[0] : null;
    }

    public static SceneObject getFirstAt(Position position) {
        ScenePosition pos = position.toScene();
        return getFirstAt(pos);
    }

    public static SceneObject getFirstAt(ScenePosition position) {
        if (!position.isLoaded()) {
            return null;
        }
        return getFirstAt(position.getX(), position.getY(), position.getFloorLevel());
    }

    /**
     * @param sceneX The x position relative to the scene
     * @param sceneY The y position relative to the scene
     * @param level  The floor level to search on
     * @return All SceneObject's at the given location
     */
    public static SceneObject[] getAt(int sceneX, int sceneY, int level) {
        if (sceneX >= 104 || sceneX < 0 || sceneY >= 104 || sceneY < 0 || level < 0 || level > 3) {
            return new SceneObject[0];
        }

        RSSceneGraph graph = Game.getClient().getSceneGraph();
        RSTile[][][] tiles = graph.getTiles();
        RSTile tile = tiles[level][sceneX][sceneY];
        if (tile != null) {
            RSSceneObject[] raw = tile.getObjects();
            if (raw.length != 0) {
                SceneObject[] wrapped = new SceneObject[raw.length];
                Arrays.setAll(wrapped, i -> raw[i].getWrapper());
                return wrapped;
            }
        }
        return new SceneObject[0];
    }

    public static SceneObject[] getAt(ScenePosition position) {
        return getAt(position.getX(), position.getY(), position.getFloorLevel());
    }

    /**
     * @param uid The uid to search for
     * @return The SceneObject matching the given uid
     */
    public static SceneObject getByUid(long uid) {
        int regionX = (int) (uid & 0x7f);
        int regionY = (int) (uid >> 7 & 0x7f);
        if (regionX >= 104 || regionX < 0 || regionY >= 104 || regionY < 0) {
            return null;
        }

        RSSceneGraph graph = Game.getClient().getSceneGraph();
        RSTile[][][] tiles = graph.getTiles();
        RSTile tile = tiles[Scene.getFloorLevel()][regionX][regionY];
        if (tile != null) {
            RSSceneObject[] raw = tile.getObjects();
            for (RSSceneObject obj : raw) {
                if (obj.getUid() == uid) {
                    return obj.getWrapper();
                }
            }
        }
        return null;
    }

    /**
     * @param floorLevel The floor level to search on
     * @param predicate  The predicate to filter the objects
     * @return All SceneObject's matching the given predicate
     */
    public static SceneObject[] getLoaded(int floorLevel, Predicate<? super SceneObject> predicate) {
        List<SceneObject> objs = new ArrayList<>();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                for (SceneObject obj : getAt(x, y, floorLevel)) {
                    if (predicate.test(obj)) {
                        objs.add(obj);
                    }
                }
            }
        }
        return objs.toArray(new SceneObject[0]);
    }

    /**
     * @param predicate The predicate to filter the objects
     * @return All SceneObject's matching the given predicate
     */
    public static SceneObject[] getLoaded(Predicate<? super SceneObject> predicate) {
        return getLoaded(Scene.getFloorLevel(), predicate);
    }

    public static SceneObject[] getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static SceneObject[] getLoaded(int floorLevel) {
        return getLoaded(floorLevel, Predicates.always());
    }

    /**
     * @param predicate The predicate to filter the objects
     * @return The nearest SceneObject matching the given predicate
     */
    public static SceneObject getNearest(Predicate<? super SceneObject> predicate) {
        SceneObject nearest = null;
        double nearestDistance = 1000;
        for (SceneObject entity : getLoaded(predicate)) {
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
     * @return The nearest SceneObject matching any of the given names
     */
    public static SceneObject getNearest(String... names) {
        return getNearest(new NamePredicate<>(names));
    }

    /**
     * @param ids The ids to search for
     * @return The nearest SceneObject matching any of the given ids
     */
    public static SceneObject getNearest(int... ids) {
        return getNearest(new IdPredicate<>(ids));
    }

    public static SceneObject[] getSorted(Comparator<? super SceneObject> comparator,
                                          Predicate<? super SceneObject> predicate) {
        SceneObject[] objects = getLoaded(predicate);
        Arrays.sort(objects, comparator);
        return objects;
    }

    public static SceneObject getBest(Comparator<? super SceneObject> comparator, Predicate<? super SceneObject> predicate, SceneObject default_) {
        SceneObject[] objects = getSorted(comparator, predicate);
        return objects.length > 0 ? objects[0] : default_;
    }

    public static SceneObject getBest(Comparator<? super SceneObject> comparator, Predicate<? super SceneObject> predicate) {
        return getBest(comparator, predicate, null);
    }

    public static SceneObject[] getAt(Position position) {
        return getAt(position.toScene().getX(), position.toScene().getY(), position.getFloorLevel());
    }

    public static SceneObjectQueryBuilder newQuery() {
        return new SceneObjectQueryBuilder();
    }
}
