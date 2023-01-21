package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.query.ItemQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author MalikDz
 */
public final class Shop {

    private static final int SHOP_PANEL_INDEX = 0;
    private static final int SHOP_ITEMS_INDEX = 16;
    private static final int INVENTORY_ITEMS_INDEX = 0;
    private static final int SHOP_INTERFACE_INDEX = InterfaceComposite.SHOP.getGroup();
    private static final int SHOP_INVENTORY_INTERFACE_INDEX = InterfaceComposite.SHOP_INVENTORY.getGroup();
    private static final InterfaceAddress CLOSE_ADDRESS = new InterfaceAddress(() -> Interfaces.getFirst(SHOP_INTERFACE_INDEX,
            comp -> comp.containsAction("Close"), true));

    private Shop() {
        throw new IllegalAccessError();
    }

    /**
     * @return {@code true} if the shop interface was successfully closed
     */
    public static boolean close() {
        InterfaceComponent close = Interfaces.lookup(CLOSE_ADDRESS);
        return close != null && close.isVisible() && close.click();
    }

    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.getComponent(SHOP_INTERFACE_INDEX, SHOP_PANEL_INDEX);
        return component != null && component.isVisible();
    }

    public static boolean buyOne(int id) {
        return buy(new IdPredicate<>(id), 1);
    }

    public static boolean buyOne(String name) {
        return buy(new NamePredicate<>(name), 1);
    }

    public static boolean buyFive(int id) {
        return buy(new IdPredicate<>(id), 5);
    }

    public static boolean buyFive(String name) {
        return buy(new NamePredicate<>(name), 5);
    }

    public static boolean buyTen(int id) {
        return buy(new IdPredicate<>(id), 10);
    }

    public static boolean buyTen(String name) {
        return buy(new NamePredicate<>(name), 10);
    }

    public static boolean buyFifty(int id) {
        return buy(new IdPredicate<>(id), 50);
    }

    public static boolean buyFifty(String name) {
        return buy(new NamePredicate<>(name), 50);
    }

    public static boolean sellOne(int id) {
        return sell(new IdPredicate<>(id), 1);
    }

    public static boolean sellOne(String name) {
        return sell(new NamePredicate<>(name), 1);
    }

    public static boolean sellFive(int id) {
        return sell(new IdPredicate<>(id), 5);
    }

    public static boolean sellFive(String name) {
        return sell(new NamePredicate<>(name), 5);
    }

    public static boolean sellTen(int id) {
        return sell(new IdPredicate<>(id), 10);
    }

    public static boolean sellTen(String name) {
        return sell(new NamePredicate<>(name), 10);
    }

    public static boolean sellFifty(int id) {
        return sell(new IdPredicate<>(id), 50);
    }

    public static boolean sellFifty(String name) {
        return sell(new NamePredicate<>(name), 50);
    }

    public static boolean contains(int... ids) {
        return contains(new IdPredicate<>(ids));
    }

    public static boolean contains(String... names) {
        return contains(new NamePredicate<>(names));
    }

    public static boolean contains(Predicate<? super Item> predicate) {
        Item[] items = getItems(predicate);
        return items.length > 0 && items[0].getStackSize() > 0;
    }

    public static boolean contains(Pattern pattern) {
        return contains(e -> e.getName().matches(pattern.pattern()));
    }

    public static Item[] getItems(Pattern pattern) {
        return getItems(e -> e.getName().matches(pattern.pattern()));
    }

    public static Item[] getItems() {
        return getItems(Predicates.always());
    }

    public static Item[] getItems(Predicate<? super Item> predicate) {
        return getItems(Interfaces.getComponent(SHOP_INTERFACE_INDEX, SHOP_ITEMS_INDEX), predicate);
    }

    public static Item[] getInventoryItems(Predicate<? super Item> predicate) {
        return getItems(Interfaces.getComponent(SHOP_INVENTORY_INTERFACE_INDEX, INVENTORY_ITEMS_INDEX), predicate);
    }

    public static boolean buy(Predicate<? super Item> predicate, int amount) {
        return isOpen() && doItemAction(getItems(predicate), "Buy " + amount);
    }

    public static boolean sell(Predicate<? super Item> predicate, int amount) {
        return isOpen() && doItemAction(getInventoryItems(predicate), "Sell " + amount);
    }

    public static int getQuantity(String name) {
        return isOpen() ? getQuantity(getItems(new NamePredicate<>(name))) : -1;
    }

    public static int getQuantity(Predicate<? super Item> predicate) {
        return isOpen() ? getQuantity(getItems(predicate)) : -1;
    }

    public static int getQuantity(Pattern pattern) {
        return getQuantity(e -> e.getName().matches(pattern.pattern()));
    }

    public static int getQuantity(int id) {
        return isOpen() ? getQuantity(getItems(new IdPredicate<>(id))) : -1;
    }

    private static int getQuantity(Item[] items) {
        return items.length > 0 ? items[0].getStackSize() : 0;
    }

    private static boolean doItemAction(Item[] items, String action) {
        Item item = items.length > 0 ? items[0] : null;
        return item != null && item.containsAction(action) && item.interact(action);
    }

    private static Item[] getItems(InterfaceComponent container, Predicate<? super Item> predicate) {
        if (!isOpen()) {
            return new Item[0];
        }
        List<Item> items = new ArrayList<>();
        for (InterfaceComponent slot : container.getComponents()) {
            int id = slot.getItemId();
            if (id > 0 && id != 6512) {
                Item item = new Item(slot);
                if (predicate.test(item)) {
                    items.add(item);
                }
            }
        }
        return items.toArray(new Item[0]);
    }

    public static boolean containsAll(String... names) {
        for (String name : names) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAll(int... ids) {
        for (int name : ids) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> Arrays.asList(getItems()));
    }
}