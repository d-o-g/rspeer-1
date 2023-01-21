package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.commons.predicate.ActionPredicate;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.query.results.PositionableQueryResults;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSSceneObject;

import java.util.*;
import java.util.function.Supplier;

public final class SceneObjectQueryBuilder extends PositionableQueryBuilder<SceneObject, SceneObjectQueryBuilder> {

    private final Supplier<List<? extends SceneObject>> provider;


    private Class<? extends RSSceneObject>[] types = null;

    private int[] ids = null;
    private int[] mapFunctions = null;
    private int[] mapScenes = null;

    private int[] colors = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] actions = null;

    public SceneObjectQueryBuilder(Supplier<List<? extends SceneObject>> provider) {
        this.provider = provider;
    }

    public SceneObjectQueryBuilder() {
        this(() -> Arrays.asList(SceneObjects.getLoaded()));
    }

    @Override
    public Supplier<List<? extends SceneObject>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected PositionableQueryResults<SceneObject> createQueryResults(Collection<? extends SceneObject> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public SceneObjectQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public SceneObjectQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public SceneObjectQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public SceneObjectQueryBuilder nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public SceneObjectQueryBuilder mapFunctions(int... mapFunctions) {
        this.mapFunctions = mapFunctions;
        return self();
    }

    public SceneObjectQueryBuilder mapScenes(int... mapScenes) {
        this.mapScenes = mapScenes;
        return self();
    }

    public SceneObjectQueryBuilder types(Class<? extends RSSceneObject>... types) {
        this.types = types;
        return self();
    }

    public SceneObjectQueryBuilder colors(int... colors) {
        this.colors = colors;
        return self();
    }

    @Override
    public SceneObjectQueryBuilder within(Positionable src, int distance) {
        provider(() -> {
            List<SceneObject> objects = new ArrayList<>();
            for (int x = -distance; x < distance; x++) {
                for (int y = -distance; y < distance; y++) {
                    Collections.addAll(objects, SceneObjects.getAt(src.getPosition().translate(x, y)));
                }
            }
            return objects;
        });
        return super.within(src, distance);
    }

    @Override
    public SceneObjectQueryBuilder within(int distance) {
        provider(() -> {
            Player src = Players.getLocal();
            List<SceneObject> objects = new ArrayList<>();
            for (int x = -distance; x < distance; x++) {
                for (int y = -distance; y < distance; y++) {
                    Collections.addAll(objects, SceneObjects.getAt(src.getPosition().translate(x, y)));
                }
            }
            return objects;
        });
        return super.within(distance);
    }

    @Override
    public SceneObjectQueryBuilder within(Area... areas) {
        provider(() -> {
            List<SceneObject> objects = new ArrayList<>();
            for (Area area : areas) {
                for (Position pos : area.getTiles()) {
                    Collections.addAll(objects, SceneObjects.getAt(pos));
                }
            }
            return objects;
        });
        return super.within(areas);
    }

    public SceneObjectQueryBuilder on(Position... positions) {
        provider(() -> {
            List<SceneObject> objects = new ArrayList<>();
            for (Position position : positions) {
                Collections.addAll(objects, SceneObjects.getAt(position));
            }
            return objects;
        });
        return super.on(positions);
    }

    @Override
    public boolean test(SceneObject obj) {
        if (ids != null && !new IdPredicate<>(ids).test(obj)) {
            return false;
        }

        if (names != null && !new NamePredicate<>(names).test(obj)) {
            return false;
        }

        if (nameContains != null && !new NamePredicate<>(true, nameContains).test(obj)) {
            return false;
        }

        if (actions != null && !new ActionPredicate<>(actions).test(obj)) {
            return false;
        }

        if (colors != null) {
            boolean match = false;
            outer:
            for (short color : obj.getColors()) {
                for (int filter : colors) {
                    if (filter == color) {
                        match = true;
                        break outer;
                    }
                }
            }


            if (!match) {
                return false;
            }
        }

        if (types != null && !ArrayUtils.contains(types, obj.getType())) {
            return false;
        }

        if (mapFunctions != null && !ArrayUtils.contains(mapFunctions, obj.getDefinition().getMapFunction())) {
            return false;
        }

        if (mapScenes != null && !ArrayUtils.contains(mapScenes, obj.getDefinition().getMapSceneId())) {
            return false;
        }

        return super.test(obj);
    }
}
