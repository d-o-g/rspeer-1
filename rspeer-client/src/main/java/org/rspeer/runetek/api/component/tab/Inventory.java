package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.query.ItemQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by MadDev on 11/19/17.
 */
public final class Inventory {

    private static final int INTERFACE_INDEX = InterfaceComposite.INVENTORY.getGroup();
    private static final int COMPONENT_INDEX = 0;

    private Inventory() {
        throw new IllegalAccessError();
    }

    /**
     * @param id The ids to search for
     * @return {@code true} if the inventory contains an item with any of the given ids
     */
    public static boolean contains(int... id) {
        return ItemTables.contains(ItemTables.INVENTORY, id);
    }

    /**
     * @param id The ids to search for
     * @return {@code true} if the inventory contains items with all of the given ids
     */
    public static boolean containsAll(int... id) {
        return ItemTables.containsAll(ItemTables.INVENTORY, id);
    }
    
    /**
     * @return the number of free slots in the inventory
     */
	public static int getFreeSlots() {
		return 28 - Inventory.getItems().length;
	}
    
    /**
     * @return The number of items in the inventory
     */
    public static int getCount() {
        return ItemTables.getCount(ItemTables.INVENTORY, Predicates.always());
    }

    public static boolean isFull() {
        return getCount() == 28;
    }

    public static boolean isEmpty() {
        return getCount() == 0;
    }

    public static int getCount(boolean includeStacks, Predicate<? super Item> predicate) {
        AtomicInteger count = new AtomicInteger();
        Inventory.accept(predicate, item -> count.addAndGet(includeStacks ? item.getStackSize() : 1));
        return count.get();
    }

    /**
     * @param includeStacks true to include stack sizes in the count
     * @param id            The item ids to search for
     * @return The number of items in the inventory matching the given ids
     */
    public static int getCount(boolean includeStacks, int... id) {
        return ItemTables.getCount(ItemTables.INVENTORY, includeStacks, id);
    }

    /**
     * @param id The item ids to search for
     * @return The number of items in the inventory matching th given ids. Does not include stack sizes
     */
    public static int getCount(int... id) {
        return getCount(false, id);
    }

    /**
     * @param names The item names to search for
     * @return {@code true} if the inventory contains an item with any of the given names
     */
    public static boolean contains(String... names) {
        return ItemTables.contains(ItemTables.INVENTORY, n -> {
            for (String name : names) {
                if (n.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * @param names The item names to search for
     * @return {@code true} if the inventory contains items with all of the given names
     */
    public static boolean containsAll(String... names) {
        for (String name : names) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the inventory contains any items matching the passed pattern
     * @param pattern The pattern that needs to be matched
     * @return true if the inventory contains any of these items
     */
    public static boolean contains(Pattern pattern) {
        return contains(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * @param includeStacks true to include stack sizes in the count
     * @param names         The item names to search for
     * @return The number of items in the inventory with the given names
     */
    public static int getCount(boolean includeStacks, String... names) {
        return ItemTables.getCount(ItemTables.INVENTORY, includeStacks, n -> {
            for (String name : names) {
                if (n.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static int getCount(boolean includeStacks, Pattern pattern) {
        return ItemTables.getCount(ItemTables.INVENTORY, includeStacks, n -> n.matches(pattern.pattern()));
    }

    public static int getCount(boolean includeStacks) {
        return ItemTables.getCount(ItemTables.INVENTORY, includeStacks, x -> true);
    }

    /**
     * @param names The item names to search for
     * @return The number of items in the inventory with the given names. Does not include stack sizes
     */
    public static int getCount(String... names) {
        return getCount(false, names);
    }

    public static int getCount(Pattern pattern) {
        return getCount(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * @param predicate The predicate to filter the items
     * @return An array of items matching the given predicate
     */
    public static Item[] getItems(Predicate<? super Item> predicate) {
        InterfaceComponent component = Interfaces.getComponent(INTERFACE_INDEX, COMPONENT_INDEX);
        List<Item> items = new ArrayList<>();
        if (component != null) {
            int slots = component.getWidth() * component.getHeight();
            for (int i = 0; i < slots; i++) {
                Item item = new Item(component, i);
                if (item.getId() > 0 && predicate.test(item)) {
                    items.add(item);
                }
            }
        }
        return items.toArray(new Item[0]);
    }

    public static Item[] getItems(Pattern pattern) {
        return getItems(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * @return All items in the inventory
     */
    public static Item[] getItems() {
        return getItems(Predicates.always());
    }

    /**
     * @param predicate The predicate used to select the item
     * @return The last item in the inventory matching the given predicate
     */
    public static Item getLast(Predicate<? super Item> predicate) {
        Item[] items = getItems(predicate);
        return items.length > 0 ? items[items.length - 1] : null;
    }

    /**
     * @param pattern The pattern for the item names to search for
     * @return The last item matching the pattern
     */
    public static Item getLast(Pattern pattern) {
        return getLast(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * @param names The item names to search for
     * @return The last item matching any of the names
     */
    public static Item getLast(String... names) {
        return getLast(new NamePredicate<>(names));
    }

    /**
     * @param ids The item ids to search for
     * @return The last item matching any of the ids
     */
    public static Item getLast(int... ids) {
        return getLast(new IdPredicate<>(ids));
    }

    /**
     * @param predicate The predicate used to select the item
     * @return The first item in the inventory matching the given predicate
     */
    public static Item getFirst(Predicate<? super Item> predicate) {
        Item[] items = getItems(predicate);
        return items.length > 0 ? items[0] : null;
    }

    public static Item getFirst(Pattern pattern) {
        return getFirst(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * @param names The item names to search for
     * @return The first item matching any of the names
     */
    public static Item getFirst(String... names) {
        return getFirst(new NamePredicate<>(names));
    }

    /**
     * @param ids The item ids to search for
     * @return The first item matching any of the ids
     */
    public static Item getFirst(int... ids) {
        return getFirst(new IdPredicate<>(ids));
    }

    /**
     * @param index The item slot index     * @return An item at the given index, or null if none
     */
    public static Item getItemAt(int index) {
        return getFirst(item -> item.getIndex() == index);
    }

    /**
     * @param predicate The predicate to filter the items
     * @return {@code true} if the inventory contains any items matching the predicate
     */
    public static boolean contains(Predicate<? super Item> predicate) {
        return getItems(predicate).length > 0;
    }

    /**
     * @param predicate The predicate to filter the items
     * @return The number of items in the inventory matching the predicate
     */
    public static int getCount(Predicate<? super Item> predicate) {
        return getItems(predicate).length;
    }

    /**
     * @return {@code true} if an item in the inventory is selected
     */
    public static boolean isItemSelected() {
        return Game.getClient().getItemSelectionState() != 0;
    }

    /**
     * @return The currently selected {@code Item} in the inventory, {@code null} if no item is selected
     */
    public static Item getSelectedItem() {
        return isItemSelected() ? getItemAt(Game.getClient().getLatestSelectedItemIndex()) : null;
    }

    public static void accept(Predicate<? super Item> predicate, Consumer<? super Item> function) {
        for (Item item : getItems(predicate)) {
            function.accept(item);
        }
    }

    public static void acceptOnce(Predicate<? super Item> predicate, Consumer<? super Item> function) {
        Item item = getFirst(predicate);
        if (item != null) {
            function.accept(item);
        }
    }

    public static void acceptUntil(Predicate<? super Item> predicate, Consumer<? super Item> function, BooleanSupplier condition) {
        for (Item item : getItems(predicate)) {
            if (condition.getAsBoolean()) {
                break;
            }
            function.accept(item);
        }
    }

    public static boolean containsAnyExcept(Predicate<? super Item> predicate) {
        int total = getCount();
        if (total == 0) {
            return false;
        }
        int count = getCount(predicate);
        return count < total;
    }

    public static boolean containsAnyExcept(String... strings) {
        return containsAnyExcept(new NamePredicate<>(strings));
    }

    public static boolean containsAnyExcept(int... ids) {
        return containsAnyExcept(new IdPredicate<>(ids));
    }

    public static boolean containsAnyExcept(Pattern pattern) {
        return containsAnyExcept(e -> e.getName().matches(pattern.pattern()));
    }

    public static boolean containsOnly(Predicate<? super Item> predicate) {
        return !contains(predicate.negate());
    }

    public static boolean containsOnly(Pattern pattern) {
        return containsOnly(e -> e.getName().matches(pattern.pattern()));
    }

    public static boolean containsOnly(int... ids) {
        return containsOnly(new IdPredicate<>(ids));
    }

    public static boolean containsOnly(String... names) {
        return containsOnly(new NamePredicate<>(names));
    }

    public static boolean deselectItem() {
        if (!isItemSelected()) {
            return true;
        }
        InterfaceComponent tab = Tab.INVENTORY.getComponent();
        return tab != null && tab.interact(x -> true);
    }

    public static boolean use(Predicate<Item> source, Interactable target) {
        Item item = getSelectedItem();
        if (item != null && !source.test(item) && deselectItem()) {
            return false;
        } else if (item != null && source.test(item) && target != null) {
            return target.interact("Use");
        } else if ((item = Inventory.getFirst(source)) != null && item.interact("Use")) {
            return false;
        }
        return false;
    }

    public static Item[] getSorted(Comparator<? super Item> comparator,
                                       Predicate<? super Item> predicate) {
        Item[] items = getItems(predicate);
        Arrays.sort(items, comparator);
        return items;
    }

    public static Item getBest(Comparator<? super Item> comparator, Predicate<? super Item> predicate, Item default_) {
        Item[] items = getSorted(comparator, predicate);
        return items.length > 0 ? items[0] : default_;
    }

    public static Item getBest(Comparator<? super Item> comparator, Predicate<? super Item> predicate) {
        return getBest(comparator, predicate, null);
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> Arrays.asList(getItems()));
    }
}
