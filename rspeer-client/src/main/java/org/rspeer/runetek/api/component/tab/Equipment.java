package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.query.ItemQueryBuilder;
import org.rspeer.runetek.providers.RSItemTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Man16
 * @author Spencer
 */
public final class Equipment {

    private Equipment() {
        throw new IllegalAccessError();
    }

    private static RSItemTable getItemTable() {
        return ItemTables.lookup(ItemTables.EQUIPMENT);
    }

    public static EquipmentSlot[] getOccupiedSlots() {
        List<EquipmentSlot> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getItemId() != -1) {
                slots.add(slot);
            }
        }
        return slots.toArray(new EquipmentSlot[0]);
    }

    public static Item[] getItems(Predicate<Item> predicate) {
        List<Item> items = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (isOccupied(slot)) {
                InterfaceComponent model = slot.getItemComponent();
                InterfaceComponent interactable = slot.getComponent();
                if (model != null && interactable != null) {
                    Item item = new Item(model, interactable, model.getComponentIndex(), slot.getItemId(), slot.getItemStackSize());
                    if (predicate.test(item)) {
                        items.add(item);
                    }
                }
            }
        }
        return items.toArray(new Item[0]);
    }

    public static Item[] getItems() {
        return getItems(Predicates.always());
    }

    private static Item getFirst(Predicate<Item> predicate) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!isOccupied(slot)) {
                continue;
            }

            InterfaceComponent model = slot.getItemComponent();
            InterfaceComponent interactable = slot.getComponent();
            if (model != null && interactable != null) {
                Item item = new Item(model, interactable, model.getComponentIndex(), slot.getItemId(), slot.getItemStackSize());
                if (predicate.test(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static boolean isOccupied(EquipmentSlot slot) {
        return slot != null && slot.getItemId() != -1;
    }

    public static boolean contains(Predicate<Item> predicate) {
        return getFirst(predicate) != null;
    }

    public static boolean contains(int... ids) {
        RSItemTable table = getItemTable();
        return table != null && table.contains(ids);
    }

    public static boolean contains(String... names) {
        return ItemTables.contains(ItemTables.EQUIPMENT, n -> {
            for (String name : names) {
                if (n.toLowerCase().contains(name.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean contains(Pattern pattern) {
        return ItemTables.contains(ItemTables.EQUIPMENT, e -> e.matches(pattern.pattern()));
    }

    public static boolean containsAll(int... ids) {
        RSItemTable table = getItemTable();
        return table != null && table.containsAll(ids);
    }

    public static boolean containsAll(String... names) {
        for (String name : names) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
    }

    public static int getCount(boolean includeStacks, int... ids) {
        RSItemTable table = getItemTable();
        return table == null ? 0 : table.getCount(includeStacks, ids);
    }

    public static int getCount(boolean includeStacks, String... names) {
        return ItemTables.getCount(ItemTables.EQUIPMENT, includeStacks, n -> {
            for (String name : names) {
                if (n.toLowerCase().contains(name.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    public static int getCount(boolean includeStacks, Pattern pattern) {
        return ItemTables.getCount(ItemTables.EQUIPMENT, includeStacks, n -> n.matches(pattern.pattern()));
    }

    public static int getCount(boolean includeStacks, Predicate<Item> predicate) {
        int count = 0;
        for (Item item : getItems(predicate)) {
            count += includeStacks ? item.getStackSize() : 1;
        }
        return count;
    }

    public static int getCount(String... names) {
        return getCount(false, names);
    }

    public static int getCount(int... ids) {
        return getCount(false, ids);
    }

    public static int getCount(Predicate<Item> predicate) {
        return getCount(false, predicate);
    }

    public static boolean interact(Predicate<Item> predicate, Predicate<String> action) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item item = slot.getItem();
            if (item != null && predicate.test(item)) {
                InterfaceComponent component = slot.getComponent();
                if (component != null) {
                    return slot.interact(action);
                }
            }
        }
        return false;
    }

    public static boolean interact(Predicate<Item> predicate, String action) {
        return interact(predicate, x -> x.equalsIgnoreCase(action));
    }

    public static boolean interact(int id, String action) {
        EquipmentSlot slot = getSlot(id);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return slot.interact(action);
            }
        }
        return false;
    }

    public static boolean interact(int id, Predicate<String> predicate) {
        EquipmentSlot slot = getSlot(id);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return slot.interact(predicate);
            }
        }
        return false;
    }

    public static boolean interact(int id, int opcode, int actionIndex) {
        EquipmentSlot slot = getSlot(id);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return slot.interact(opcode, actionIndex);
            }
        }
        return false;
    }

    public static boolean interact(int id, int opcode) {
        return interact(id, opcode, 0);
    }

    public static boolean interact(String name, String action) {
        EquipmentSlot slot = getSlot(name);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return slot.interact(action);
            }
        }
        return false;
    }

    public static boolean interact(String name, Predicate<String> predicate) {
        EquipmentSlot slot = getSlot(name);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return slot.interact(predicate);
            }
        }
        return false;
    }

    public static boolean interact(String name, int opcode, int actionIndex) {
        EquipmentSlot slot = getSlot(name);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return slot.interact(opcode, actionIndex);
            }
        }
        return false;
    }

    public static boolean interact(String name, int opcode) {
        return interact(name, opcode, 0);
    }

    public static boolean unequip(int id) {
        EquipmentSlot slot = getSlot(id);
        return slot != null && slot.unequip();
    }

    public static boolean unequip(String name) {
        EquipmentSlot slot = getSlot(name);
        return slot != null && slot.unequip();
    }

    public static boolean unequip(Predicate<Item> predicate) {
        EquipmentSlot slot = getSlot(predicate);
        return slot != null && slot.unequip();
    }

    public static EquipmentSlot getSlot(Predicate<Item> predicate) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item item = slot.getItem();
            if (item != null && predicate.test(item)) {
                return slot;
            }
        }
        return null;
    }

    public static EquipmentSlot getSlot(int id) {
        if (!contains(id)) {
            return null;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getItemId() == id) {
                return slot;
            }
        }
        return null;
    }

    public static EquipmentSlot getSlot(String name) {
        if (!contains(name)) {
            return null;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getItemName().equalsIgnoreCase(name)) {
                return slot;
            }
        }
        return null;
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> Arrays.asList(getItems()));
    }
}
