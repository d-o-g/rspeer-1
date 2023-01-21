package org.rspeer.runetek.api;

import org.rspeer.runetek.providers.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Definitions {

    private static final Map<Integer, RSItemDefinition> items = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Integer, RSObjectDefinition> objects = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Integer, RSNpcDefinition> npcs = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Integer, RSVarpbit> varpbits = Collections.synchronizedMap(new LinkedHashMap<>());

    private static final int LOAD_LIMIT = 1 << 16;

    /**
     * Populates the definition maps. To avoid corrupting the game streams and crashing the game,
     * this should only be called in a safe state (e.g. when logged out)
     */
    public static synchronized void populate() {
        RSClient client = Game.getClient();
        if (client != null) {
            boolean membersWorld = client.isMembersWorld();
            client.setLoadMembersItemDefinitions(true);
            loadDefinitions(items, id -> Game.getClient().getItemDefinition(id), Function.identity(), LOAD_LIMIT);
            client.setLoadMembersItemDefinitions(membersWorld);
            loadDefinitions(npcs, id -> Game.getClient().getNpcDefinition(id), RSNpcDefinition::transform, LOAD_LIMIT);
            loadDefinitions(objects, id -> Game.getClient().getObjectDefinition(id), RSObjectDefinition::transform, LOAD_LIMIT);
            loadDefinitions(varpbits, id -> Game.getClient().getVarpbit(id), Function.identity(), LOAD_LIMIT);
        }
    }

    private static <T> void loadDefinitions(Map<Integer, T> dest,
                                            Function<Integer, T> invoker,
                                            Function<T, T> transformer,
                                            int limit) {
        for (int i = 0; i < limit; i++) {
            T definition = invoker.apply(i);
            if (definition != null) {
                T transformed = transformer.apply(definition);
                if (transformed != null) {
                    dest.put(i, transformed);
                } else if (definition instanceof RSDefinition) {
                    dest.put(i, definition);
                }
            }
        }
    }

    /**
     * @param id The definition id
     * @return An RSNpcDefinition with the given id, or null if not present
     */
    public static RSNpcDefinition getNpc(int id) {
        return npcs.get(id);
    }

    /**
     * @param id The definition id
     * @return An RSObjectDefinition with the given id, or null if not present
     */
    public static RSObjectDefinition getObject(int id) {
        return objects.get(id);
    }

    /**
     * @param id The definition id
     * @return An RSItemDefinition with the given id, or null if not present
     */
    public static RSItemDefinition getItem(int id) {
        return items.get(id);
    }

    /**
     * @param name The name of the item to search for
     * @param predicate
     * @return The first item definition with the given name, matching the given predicate
     */
    public static RSItemDefinition getItem(String name, Predicate<RSItemDefinition> predicate) {
        for (int i = 0; i < LOAD_LIMIT; i++) {
            RSItemDefinition d = items.get(i);
            if (d != null && d.getName() != null && d.getName().equalsIgnoreCase(name) && predicate.test(d)) {
                return d;
            }
        }
        return null;
    }

    public static RSVarpbit getVarpbit(int id) {
        return varpbits.get(id);
    }

    public static Map<Integer, RSObjectDefinition> getObjects() {
        return objects;
    }

    public static Map<Integer, RSItemDefinition> getItems() {
        return items;
    }

    public static Map<Integer, RSNpcDefinition> getNpcs() {
        return npcs;
    }

    public static Map<Integer, RSVarpbit> getVarpbits() {
        return varpbits;
    }
}
