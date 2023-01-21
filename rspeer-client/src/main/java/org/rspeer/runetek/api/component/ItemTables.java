package org.rspeer.runetek.api.component;

import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.rspeer.runetek.providers.RSItemTable;

import java.util.function.Predicate;

public final class ItemTables {

    public static final int VARROCK_GENERAL_STORE = 4;
    public static final int VARROCK_RUNE_STORE = 5;
    public static final int VARROCK_STAFF_STORE = 51;
    public static final int PRICE_CHECKER = 90;
    public static final int INVENTORY = 93;
    public static final int EQUIPMENT = 94;
    public static final int BANK = 95;
    public static final int BARROWS = 141;
    public static final int EXCHANGE_COLLECTION = 518;

    private ItemTables() {
        throw new IllegalAccessError();
    }

    public static RSItemTable lookup(int key) {
        return Functions.mapOrNull(() -> Game.getClient().getItemTables(), t -> t.safeLookup(key));
    }

    public static boolean contains(int key, int... itemIds) {
        return Functions.mapOrElse(() -> lookup(key), table -> table.contains(itemIds));
    }

    public static boolean containsAll(int key, int... itemIds) {
        return Functions.mapOrElse(() -> lookup(key), table -> table.containsAll(itemIds));
    }

    public static int getCount(int key, boolean includeStacks, int... itemIds) {
        return Functions.mapOrDefault(() -> lookup(key), table -> table.getCount(includeStacks, itemIds), 0);
    }

    public static boolean contains(int key, Predicate<String> names) {
        RSItemTable table = lookup(key);
        if (table == null) {
            return false;
        }
        int[] stacks = table.getStackSizes();
        int[] ids = table.getIds();
        if (ids == null || stacks == null) {
            return false;
        }

        for (int currentId : ids) {
            RSItemDefinition def = Definitions.getItem(currentId);
            if (def != null && def.getName() != null) {
                if (names.test(def.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getCount(int key, int... itemIds) {
        return getCount(key, false, itemIds);
    }

    public static int getCount(int key, boolean includeStacks, Predicate<String> names) {
        RSItemTable table = lookup(key);
        if (table == null) {
            return 0;
        }
        int[] stacks = table.getStackSizes();
        int[] ids = table.getIds();
        if (ids == null || stacks == null) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < ids.length; i++) {
            int currentId = ids[i];
            int currentStack = stacks[i];
            RSItemDefinition def = Definitions.getItem(currentId);
            if (def != null && def.getName() != null) {
                if (names.test(def.getName().toLowerCase())) {
                    count += includeStacks ? currentStack : 1;
                }
            }
        }
        return count;
    }

    public static int getCount(int key, Predicate<String> names) {
        return getCount(key, false, names);
    }
}
