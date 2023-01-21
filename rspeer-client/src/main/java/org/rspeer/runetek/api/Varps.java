package org.rspeer.runetek.api;

import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.providers.RSVarpbit;

public final class Varps {

    public static final int MAX_VARP = 4000;
    public static final int[] BIT_MASKS = new int[32];

    static {
        int delta = 2;
        for (int mask = 0; mask < 32; ++mask) {
            BIT_MASKS[mask] = delta - 1;
            delta += delta;
        }
    }

    private Varps() {
        throw new IllegalAccessError();
    }

    /**
     * @return An {@code int[]} array of all varps
     */
    public static int[] getAll() {
        int[] vars = Game.getClient().getVarps();
        return vars == null ? new int[0] : vars;
    }

    /**
     * @param index The index within the array
     * @return A varp at the given index, or -1 if invalid index was specified
     * @throws java.lang.IllegalArgumentException if a bad index was passed
     */
    public static int get(int index) {
        int[] vars = getAll();
        if (vars.length == 0 || index >= MAX_VARP || index >= vars.length || index < 0) {
            return -1;
        }
        return vars[index];
    }

    public static boolean getBoolean(int index) {
        return get(index) == 1;
    }

    /**
     * @param id The varpbit id
     * @return A Varpbit with the given id, null if none found
     */
    public static Varpbit getBit(int id) {
        return Functions.mapOrDefault(() -> Definitions.getVarpbit(id), v -> new Varpbit(v, id), null);
    }

    /**
     * @param id The varpbit id
     * @return The value of the varpbit at the given index
     */
    public static int getBitValue(int id) {
        return Functions.mapOrM1(() -> getBit(id), RSVarpbit::getValue);
    }
}
