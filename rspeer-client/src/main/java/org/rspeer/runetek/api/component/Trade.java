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

/**
 * @author Man16
 * @author Spencer
 */
public final class Trade {

    private static final String TEXT_OFFER_ALL = "Offer-All";

    private static final InterfaceAddress FIRST_INTERFACE = new InterfaceAddress(InterfaceComposite.TRADE);
    private static final InterfaceAddress FIRST_ACCEPT = FIRST_INTERFACE.component(10);
    private static final InterfaceAddress FIRST_DECLINE = FIRST_INTERFACE.component(13);

    private static final InterfaceAddress FIRST_MY_ITEMS = FIRST_INTERFACE.component(25);
    private static final InterfaceAddress FIRST_THEIR_ITEMS = FIRST_INTERFACE.component(28);

    private static final InterfaceAddress SECOND_INTERFACE = new InterfaceAddress(InterfaceComposite.TRADE_SECOND);
    private static final InterfaceAddress SECOND_ACCEPT = SECOND_INTERFACE.component(13);
    private static final InterfaceAddress SECOND_DECLINE = SECOND_INTERFACE.component(14);

    private static final InterfaceAddress SECOND_MY_ITEMS = SECOND_INTERFACE.component(28);
    private static final InterfaceAddress SECOND_THEIR_ITEMS = SECOND_INTERFACE.component(29);

    private static final InterfaceAddress INVENTORY = new InterfaceAddress(InterfaceComposite.TRADE_INVENTORY).component(0);

    private Trade() {
        throw new IllegalAccessError();
    }

    /**
     * @return {@code true} if any of the trade interfaces are currently open
     */
    public static boolean isOpen() {
        return Interfaces.isOpen(FIRST_INTERFACE) || Interfaces.isOpen(SECOND_INTERFACE);
    }

    /**
     * @param secondScreen {@code} true to check for second screen, {@code false} to check for first
     * @return {@code true} if the given screen is open
     */
    public static boolean isOpen(boolean secondScreen) {
        return Interfaces.isOpen(secondScreen ? SECOND_INTERFACE : FIRST_INTERFACE);
    }

    private static void extractItems(Predicate<? super Item> predicate, List<Item> dest, InterfaceAddress address) {
        for (InterfaceComponent component : Interfaces.lookup(address).getComponents()) {
            if (component != null && component.getItemId() != -1 && component.getActions().length > 0) {
                Item item = new Item(component);
                if (predicate.test(item)) {
                    dest.add(new Item(component));
                }
            }
        }
    }

    public static Item[] getMyItems(Predicate<? super Item> predicate) {
        if (!isOpen(false)) {
            return new Item[0];
        }
        List<Item> items = new ArrayList<>();
        extractItems(predicate, items, FIRST_MY_ITEMS);
        return items.toArray(new Item[0]);
    }

    public static Item[] getMyItems() {
        return getMyItems(Predicates.always());
    }

    public static Item[] getTheirItems(Predicate<? super Item> predicate) {
        if (!isOpen(false)) {
            return new Item[0];
        }
        List<Item> items = new ArrayList<>();
        extractItems(predicate, items, FIRST_THEIR_ITEMS);
        return items.toArray(new Item[0]);
    }

    public static Item[] getTheirItems() {
        return getTheirItems(Predicates.always());
    }

    public static boolean contains(boolean ourItems, Predicate<? super Item> predicate) {
        Item[] items = ourItems ? getMyItems(predicate) : getTheirItems(predicate);
        return items.length > 0;
    }

    public static boolean contains(boolean ourItems, String... names) {
        return contains(ourItems, new NamePredicate<>(names));
    }

    public static boolean contains(boolean ourItems, int... ids) {
        return contains(ourItems, new IdPredicate<>(ids));
    }

    private static Item[] getInventory(Predicate<? super Item> predicate) {
        InterfaceComponent inventory = Interfaces.lookup(INVENTORY);
        if (inventory == null) {
            return new Item[0];
        }

        List<Item> items = new ArrayList<>();
        for (InterfaceComponent component : inventory.getComponents()) {
            if (component.getConfig() == 0) {
                break;
            }
            Item item = new Item(component);
            if (predicate.test(item)) {
                items.add(item);
            }
        }
        return items.toArray(new Item[0]);
    }

    private static Item getFirstFromInventory(Predicate<? super Item> predicate) {
        Item[] items = getInventory(predicate);
        return items.length > 0 ? items[0] : null;
    }

    public static boolean offer(Predicate<? super Item> predicate, Predicate<String> action) {
        Item item = getFirstFromInventory(predicate);
        return item != null && item.interact(action);
    }

    public static boolean offer(int id, Predicate<String> action) {
        return offer(new IdPredicate<>(id), action);
    }

    public static boolean offer(String name, Predicate<String> action) {
        return offer(new NamePredicate<>(name), action);
    }

    public static boolean offerAll(Predicate<? super Item> predicate) {
        return offer(predicate, x -> x.equals(TEXT_OFFER_ALL));
    }

    public static boolean offerAll(int id) {
        return offer(id, x -> x.equals(TEXT_OFFER_ALL));
    }

    public static boolean offerAll(String name) {
        return offer(name, x -> x.equals(TEXT_OFFER_ALL));
    }

    public static boolean accept() {
        if (isOpen(false)) {
            InterfaceComponent first = Interfaces.lookup(FIRST_ACCEPT);
            return first != null && first.interact(x -> true);
        }
        InterfaceComponent second = Interfaces.lookup(SECOND_ACCEPT);
        return second != null && second.interact(x -> true);
    }

    public static boolean decline() {
        if (isOpen(false)) {
            InterfaceComponent first = Interfaces.lookup(FIRST_DECLINE);
            return first != null && first.interact(x -> true);
        }
        InterfaceComponent second = Interfaces.lookup(SECOND_DECLINE);
        return second != null && second.interact(x -> true);
    }

    public static boolean hasOtherAccepted() {
        InterfaceComponent waitingFirst = Interfaces.getComponent(335, 30);
        InterfaceComponent waitingSecond = Interfaces.getComponent(334, 4);

        if (isOpen(false)) {
            return waitingFirst != null && waitingFirst.isVisible() && waitingFirst.getText().equals("Other player has accepted.");
        }
        return waitingSecond != null && waitingSecond.isVisible() && waitingSecond.getText().equals("Other player has accepted.");
    }

    public static boolean isWaitingForMe() {
        InterfaceComponent waitingFirst = Interfaces.getComponent(335, 30);
        InterfaceComponent waitingSecond = Interfaces.getComponent(334, 4);
        if (isOpen(false)) {
            return waitingFirst != null && waitingFirst.isVisible() && (waitingFirst.getText().equals("") || waitingFirst.getText().equals("Other player has accepted."));
        }
        return waitingSecond != null && waitingSecond.isVisible() && (waitingSecond.getText().equals("Are you sure you want to make this trade?") || waitingSecond.getText().equals("Other player has accepted."));
    }

    public static ItemQueryBuilder newQuery(boolean localItems) {
        return new ItemQueryBuilder(() -> Arrays.asList(localItems ? getMyItems() : getTheirItems()));
    }
}
