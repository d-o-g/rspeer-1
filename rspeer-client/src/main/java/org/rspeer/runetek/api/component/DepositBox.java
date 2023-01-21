package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.query.ItemQueryBuilder;
import org.rspeer.runetek.api.scene.SceneObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author MalikDz
 * @author Spencer
 */
public final class DepositBox {

    private static final int MAIN_COMPONENT = 1;
    private static final int PANEL_COMPONENT = 0;
    private static final int CLOSE_BUTTON_COMPONENT = 11;
    private static final int SLOT_CONTAINER_COMPONENT = 2;
    private static final int GROUP = InterfaceComposite.DEPOSIT_BOX.getGroup();

    private static final InterfaceAddress DEPOSIT_INVENTORY_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Deposit inventory"))
    );

    private static final InterfaceAddress DEPOSIT_EQUIPMENT_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Deposit worn items"))
    );

    private static final InterfaceAddress DEPOSIT_LOOTBAG_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Deposit loot"))
    );

    private DepositBox() {
        throw new IllegalAccessError();
    }

    public static boolean depositAllExcept(Predicate<? super Item> predicate) {
        return depositAll(predicate.negate());
    }

    public static boolean depositAllExcept(int... ids) {
        return depositAllExcept(new IdPredicate<>(ids));
    }

    public static boolean depositAllExcept(String... names) {
        return depositAllExcept(new NamePredicate<>(names));
    }

    public static boolean depositAll(int... id) {
        return depositAll(new IdPredicate<>(id));
    }

    public static boolean depositAll(String... name) {
        return depositAll(new NamePredicate<>(name));
    }

    public static boolean deposit(int id, int amount) {
        return deposit(new IdPredicate<>(id), true, amount);
    }

    public static boolean deposit(String name, int amount) {
        return deposit(new NamePredicate<>(name), true, amount);
    }

    public static boolean depositAll(Predicate<? super Item> predicate) {
        return deposit(predicate, false, -1);
    }

    public static boolean open() {
        return open(BankLocation.getNearestDepositBox());
    }

    public static boolean open(BankLocation loc) {
        if (loc == null || loc.getType() != BankLocation.Type.DEPOSIT_BOX) {
            return false;
        }
        Position p = loc.getPosition();
        if (p.distance() > 7 || !Movement.isInteractable(p, false)) {
            if (Movement.walkTo(p)) {
                Time.sleep(250, 550);
            }
            return false;
        }
        Interactable depositBox = SceneObjects.getNearest(loc.getName());
        return depositBox != null && depositBox.interact(loc.getAction()) && Time.sleepUntil(DepositBox::isOpen, 1200);
    }

    /**
     * @param predicate
     * @param depositFirstItem True if you only want to deposit one item
     * @param amount           The amount of the item you want to deposit
     * @return True if all the deposit interactions are successful
     */

    public static boolean deposit(Predicate<? super Item> predicate, boolean depositFirstItem, int amount) {
        if (!isOpen()) {
            return false;
        }
        Item[] items = getItems(predicate);
        boolean interactionSucceeded = true;
        for (int x = 0; x < items.length; x++) {
            if (depositFirstItem && x == 1) {
                break;
            } else if (items[x] == null) {
                interactionSucceeded = false;
            } else if (amount == -1 && items[x].containsAction("Deposit-All")) {
                interactionSucceeded &= items[x].interact("Deposit-All");
            } else if (items[x].containsAction("Deposit-" + amount)) {
                interactionSucceeded &= items[x].interact("Deposit-" + amount);
            } else if (items[x].interact("Deposit-X") && Time.sleepUntil(EnterInput::isOpen, 1500)) {
                Time.sleep(200);
                Keyboard.sendText(String.valueOf(amount));
                Keyboard.pressEnter();
            }
        }
        return interactionSucceeded;
    }

    /**
     * @return {@code true} if the deposit box is open
     */
    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.getComponent(GROUP, PANEL_COMPONENT);
        return component != null && component.isVisible();
    }

    /**
     * @return {@code true} if the deposit inventory button in the deposit box was clicked successfully
     */
    public static boolean depositInventory() {
        InterfaceComponent button = Interfaces.lookup(DEPOSIT_INVENTORY_ADDRESS);
        return button != null && button.interact("Deposit inventory");
    }

    /**
     * @return {@code true} if the deposit inventory button in the deposit box was clicked successfully
     */
    public static boolean depositEquipment() {
        InterfaceComponent button = Interfaces.lookup(DEPOSIT_EQUIPMENT_ADDRESS);
        return button != null && button.interact("Deposit worn items");
    }

    /**
     * @return {@code true} if the deposit loot button in the deposit box was clicked successfully
     */
    public static boolean depositLootingBag() {
        InterfaceComponent button = Interfaces.lookup(DEPOSIT_LOOTBAG_ADDRESS);
        return button != null && button.interact("Deposit loot");
    }

    /**
     * @return {@code true} if the deposit close button in the deposit box was clicked successfully
     */
    public static boolean close() {
        InterfaceComponent button = Interfaces.getComponent(GROUP, MAIN_COMPONENT, CLOSE_BUTTON_COMPONENT);
        return button != null && button.interact("Close");
    }

    /**
     * @param predicate The predicate which should be used to select the items
     * @return An array of {@link Item}'s in the deposit box that were accepted by
     * the given predicate
     */
    public static Item[] getItems(Predicate<? super Item> predicate) {
        if (!isOpen()) {
            return new Item[0];
        }
        InterfaceComponent container = Interfaces.getComponent(GROUP, SLOT_CONTAINER_COMPONENT);
        if (container == null) {
            return new Item[0];
        }
        List<Item> items = new ArrayList<>();
        InterfaceComponent[] slots = container.getComponents();
        for (InterfaceComponent slot : slots) {
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

    public static Item[] getItems() {
        return getItems(Predicates.always());
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> Arrays.asList(getItems()));
    }
}
