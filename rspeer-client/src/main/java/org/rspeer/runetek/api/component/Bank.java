package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.query.ItemQueryBuilder;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSInterfaceComponent;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Bank {

    private static final int GROUP = InterfaceComposite.BANK.getGroup();
    private static final int PANEL_COMPONENT = 0;

    private static final InterfaceAddress SLOT_CONTAINER_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.getComponentCount() > 800)
    );

    private static final InterfaceAddress DEPOSIT_INVENTORY_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Deposit inventory"))
    );

    private static final InterfaceAddress DEPOSIT_EQUIPMENT_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Deposit worn items"))
    );

    private static final InterfaceAddress CLOSE_BUTTON_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Close"), true)
    );

    private static final Varpbit BIT_OPEN_TAB_INDEX;
    private static final Varpbit BIT_TAB_DISPLAY;
    private static final Varpbit BIT_TAB_1;
    private static final Varpbit BIT_TAB_2;
    private static final Varpbit BIT_TAB_3;
    private static final Varpbit BIT_TAB_4;
    private static final Varpbit BIT_TAB_5;
    private static final Varpbit BIT_TAB_6;
    private static final Varpbit BIT_TAB_7;
    private static final Varpbit BIT_TAB_8;
    private static final Varpbit BIT_TAB_9;
    private static final int NUM_TABS = 9;
    private static final Varpbit BIT_WITHDRAW_MODE;
    private static final Varpbit BIT_REARRANGE_MODE;
    /**
     * The EnumSet of all non-main tabs.
     */
    private static final EnumSet<Tab> MINOR_TABS = EnumSet.range(Tab.TAB_1, Tab.TAB_9);
    private static final int BANK_CAPACITY_OFFSET = 4;
    private static final InterfaceAddress BANK_CAPACITY_ADDRESS = new InterfaceAddress(
            () -> {
                InterfaceComponent ic = Interfaces.getFirst(GROUP, ic2 -> ic2.getText().equals("The Bank of Gielinor"));
                return Interfaces.getComponent(GROUP, ic.getComponentIndex() + BANK_CAPACITY_OFFSET);
            }
    );

    static {
        BIT_OPEN_TAB_INDEX = Varps.getBit(4150); // varp 115 bits [2,5]
        BIT_TAB_DISPLAY = Varps.getBit(4170); // varp 867 bits [30,31]
        BIT_TAB_1 = Varps.getBit(4171); // varp 867 bits [0,9]
        BIT_TAB_2 = Varps.getBit(4172); // varp 867 bits [10,19]
        BIT_TAB_3 = Varps.getBit(4173); // varp 867 bits [20,29]
        // -----------------------------------------------
        BIT_TAB_4 = Varps.getBit(4174); // varp 1052 bits [0,9]
        BIT_TAB_5 = Varps.getBit(4175); // varp 1052 bits [10,19]
        BIT_TAB_6 = Varps.getBit(4176); // varp 1052 bits [20,29]
        // -----------------------------------------------
        BIT_TAB_7 = Varps.getBit(4177); // varp 1053 bits [0,9]
        BIT_TAB_8 = Varps.getBit(4178); // varp 1053 bits [10,19]
        BIT_TAB_9 = Varps.getBit(4179); // varp 1053 bits [20,29]

        BIT_WITHDRAW_MODE = Varps.getBit(3958);
        BIT_REARRANGE_MODE = Varps.getBit(3959);
    }

    private Bank() {
        throw new IllegalAccessError();
    }

    /**
     * @return {@code true} if the bank is open and seen as visible by the game
     */
    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.getComponent(GROUP, PANEL_COMPONENT);
        return component != null && component.isVisible();
    }

    /**
     * @param predicate The predicate which should be used to select the items
     * @return An array of {@link Item}'s in the bank that were accepted by the
     * given predicate
     */
    public static Item[] getItems(Predicate<? super Item> predicate) {
        if (!isOpen()) {
            return new Item[0];
        }
        InterfaceComponent container = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
        if (container != null) {
            List<Item> items = new ArrayList<>();
            InterfaceComponent[] slots = container.getComponents();
            for (InterfaceComponent slot : slots) {
                if (slot.isVisible()) {
                    int id = slot.getItemId();
                    if (id > 0 && id != 6512) {
                        Item item = new Item(slot);
                        if (predicate.test(item)) {
                            items.add(item);
                        }
                    }
                }
            }
            return items.toArray(new Item[0]);
        }
        return new Item[0];
    }

    /**
     * @return The current Bank {@link Bank.TabDisplay} type
     */
    public static TabDisplay getTabDisplay() {
        int v = BIT_TAB_DISPLAY.getValue();
        return TabDisplay.values()[v];
    }

    /**
     * @return All the {@link Item}'s in the bank
     */
    public static Item[] getItems() {
        return getItems(Predicates.always());
    }

    /**
     * @param predicate the predicate to select the item
     * @return the first {@link Item} selected by the predicate
     */
    public static Item getFirst(Predicate<? super Item> predicate) {
        for (Item item : getItems()) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * @param ids The Item id's to search for
     * @return Gets the first {@link Item} with the given ids
     */
    public static Item getFirst(int... ids) {
        return getFirst(new IdPredicate<>(ids));
    }

    /**
     * @param names The item names to search for
     * @return Gets the first {@link Item} with the given names
     */
    public static Item getFirst(String... names) {
        return getFirst(new NamePredicate<>(names));
    }

    public static Item getFirst(Pattern pattern) {
        return getFirst(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * Selects the close button of the bank if it is open
     *
     * @return {@code true} if the bank was successfully closed
     */
    public static boolean close() {
        if (!Bank.isOpen()) {
            return true;
        }
        InterfaceComponent dynamicCloseButton = CLOSE_BUTTON_ADDRESS.resolve();
        InterfaceComponent closeButton = dynamicCloseButton == null
                ? Interfaces.getComponent(GROUP, 3, 11)
                : dynamicCloseButton;
        return closeButton != null && closeButton.interact(x -> true) && Time.sleepUntil(Bank::isClosed, 700);
    }

    /**
     * @return {@code true} if the current {@link Bank.Tab} is the main bank tab
     */
    public static boolean isMainTabOpen() {
        return getOpenTab() == Tab.MAIN_TAB;
    }

    /**
     * Determines if the bank is closed. The logic is if the bank is not open, then
     * it is closed {!{@link #isOpen()}}
     *
     * @return {@code true} If and only if the bank is not open.
     */
    public static boolean isClosed() {
        return !isOpen();
    }

    /**
     * Determines the bank tab index that is currently in focus, or will be in focus
     * next time the bank is open.
     * <p>
     * should note that this value does not reset to any default value when the bank
     * is closed. When the bank is closed the the tab that was open when the bank is
     * closed will be the same tab that is in focus when the bank is re-opened. This
     * observation has yet to be proven, but was observed in revision 70 of the game
     * client.
     *
     * @return The tab that is or will be in focus currently, or the next time the
     * bank is opened.
     */
    private static int getOpenTabIndex() {
        return BIT_OPEN_TAB_INDEX.getValue();
    }

    /**
     * Determines the {@link Tab} that is currently in focus. This method requires
     * the bank to be open. In the case that the bank was closed, null will be
     * returned. One should also note that the tab that was in focus when the bank
     * is closed will be the same tab that will be in focus next time the bank is
     * open; it's observed that it does not reset to any default value. As to
     * clarify the purpose of this method, it was decided to return null in the case
     * that the bank was closed, though it's never null.
     *
     * @return The tab that is currently in focus. Null if the bank is closed.
     */
    public static Tab getOpenTab() {
        return isClosed() ? null : Tab.get(getOpenTabIndex());
    }

    /**
     * If the bank is closed, the tab that was last in focus/open is observed to be
     * the same tab that will be opened next time the bank is opened. For
     * clarification purposes the method {@link #getOpenTab()} returns null in the
     * case that the bank was closed, when in actuality it does not reset to any
     * default or invalid value. This method allows the user to predict that tab
     * that will be open next time the bank is open.
     *
     * @return The tab that is currently in focus, or will be in focus next time the
     * bank is opened.
     */
    public static Tab getTabFuture() {
        return Tab.get(getOpenTabIndex());
    }

    /**
     * <p>Will do all the interactions required to open the specified bank location.
     * First the method will check if you are close enough to the bank location for
     * it to be interactable with. If this is not the case, the method will start walking
     * towards the specified BankLocation.</p>
     * <p>
     * <p>Once the player is close enough to the bank location and can interact with the location
     * the method will start interaction with the Npc/SceneObject. After successfully interacting
     * the method will sleepUntil the bank is open for 1200ms</p>
     *
     * @param loc The {@link BankLocation} to be opened.
     * @return True if the bank was opened.
     */
    public static boolean open(BankLocation loc) {
        if (loc == null) {
            return false;
        }
        Position p = loc.getPosition();
        if (!Scene.isLoaded(p) || !Movement.isInteractable(p, false) || p.distance() >= 15) {
            if (Movement.walkTo(p)) {
                Time.sleep(250, 550);
            }
            return false;
        }

        Interactable bank = null;
        switch (loc.getType()) {
            case NPC: {
                bank = Npcs.getNearest(loc.getName());
                break;
            }
            case DEPOSIT_BOX:
            case BANK_CHEST:
            case BANK_BOOTH: {
                bank = SceneObjects.getNearest(i -> i.containsAction(loc.getAction()) && i.getName().equals(loc.getName()));
            }
        }
        BooleanSupplier condition = () -> loc.getType() == BankLocation.Type.DEPOSIT_BOX ? DepositBox.isOpen() : Bank.isOpen();
        return bank != null && bank.interact(loc.getAction()) && Time.sleepUntil(condition, 2400);
    }

    /**
     * This method opens the bank by passing {@link BankLocation#getNearestWithdrawable()} into
     * {@link Bank#open(BankLocation)}.
     *
     * @return True if the bank was opened.
     * @see Bank#open(BankLocation)
     */
    public static boolean open() {
        return open(BankLocation.getNearestWithdrawable());
    }

    /**
     * Determines the next non-main tab that does not occupies at least one item,
     * and thus is collapsed, and can be used to open/create a tab. This performs a
     * linear check from tab 0 to the rest of the tabs, and returns the first tab to
     * be {@link Bank.Tab#isCollapsed()} One should note that the main tab can never
     * be collapsed, and will never be a returned value.
     *
     * @return The first occurrence of a collapsed tab.
     */
    public static Tab getNextCollapsedTab() {
        for (Tab tab : MINOR_TABS) {
            if (tab.isCollapsed()) {
                return tab;
            }
        }
        return null;
    }

    /**
     * @return {@code true} if the deposit inventory button in the bank was clicked
     * successfully
     */
    public static boolean depositInventory() {
        InterfaceComponent button = Interfaces.lookup(DEPOSIT_INVENTORY_ADDRESS);
        return button != null && button.interact("Deposit inventory");
    }

    /**
     * @return {@code true} if the deposit inventory button in the bank was clicked
     * successfully
     */
    public static boolean depositEquipment() {
        InterfaceComponent button = Interfaces.lookup(DEPOSIT_EQUIPMENT_ADDRESS);
        return button != null && button.interact("Deposit worn items");
    }

    /**
     * Deposits all items into the bank that are not accepted by the filter
     *
     * @param predicate The predicate which will be used to select the items
     */
    public static boolean depositAllExcept(Predicate<? super Item> predicate) {
        return depositAll(predicate.negate());
    }

    /**
     * @param ids The item ids to not deposit
     */
    public static boolean depositAllExcept(int... ids) {
        return depositAllExcept(new IdPredicate<>(ids));
    }

    /**
     * @param names The names of the items to not deposit
     */
    public static boolean depositAllExcept(String... names) {
        return depositAllExcept(new NamePredicate<>(names));
    }

    public static boolean depositAllExcept(Pattern pattern) {
        return depositAllExcept(e -> e.getName().matches(pattern.pattern()));
    }

    // widgets are different for bank inventory and the deposit actions are assigned
    // to these so...

    /**
     * Gets the "Bank inventory" which is a different component from your normal inventory.
     * When the bank is opened the normal inventory is hidden and the bank inventory is shown;
     * The deposit actions are assigned to this bank inventory, so to interact with any of the items
     * in your inventory when banking, you need to perform actions on these items and not the
     * {@link Inventory#getItems()}.
     *
     * @param predicate The predicate which the items need to match for them to be returned.
     * @return An array of {@link Item}s that conform to the passed predicate.
     */
    public static Item[] getInventory(Predicate<? super Item> predicate) {
        if (!isOpen()) {
            return new Item[0];
        }
        InterfaceComponent container = Interfaces.getComponent(15, 3);
        if (predicate == null || container == null) {
            return new Item[0];
        }

        List<Item> items = new ArrayList<>();
        for (InterfaceComponent item : container.getComponents()) {
            if (item != null && item.getItemId() != 6512) {
                Item wrapped = new Item(item);
                if (predicate.test(wrapped)) {
                    items.add(wrapped);
                }
            }
        }
        return items.toArray(new Item[0]);
    }

    /**
     * Deposits all items into the bank that are accepted by the filter
     *
     * @param predicate The predicate which will be used to select the items
     * @see #depositInventory - should be used instead of this method if the entire
     * inventory is needed to be deposited
     */
    public static boolean depositAll(Predicate<? super Item> predicate) {
        if (!isOpen()) {
            return false;
        }
        Item[] items = getInventory(predicate);
        if (items.length == getInventory(Predicates.always()).length) {
            return depositInventory();
        }
        Set<Integer> visited = new HashSet<>();
        for (Item item : items) {
            if (!visited.contains(item.getId()) && item.getDefinition() != null) {
                if (!item.interact("Deposit-All")) {
                    return false;
                }
                visited.add(item.getId());
                Time.sleep(Random.mid(100, 200));
            }
        }
        return true;
    }

    /**
     * Deposits 'amount' of items that match the predicate into the bank.
     * If the amount does not match any of the present menu actions Deposit-X is used.
     * This method will only perform one action per loop, and thus only deposit 'amount' of the first
     * matching item.
     *
     * @param predicate The predicate which will be used to select the items.
     * @param amount    The amount that needs to be deposited.
     * @return {@code true} if the deposit was successful.
     */
    public static boolean deposit(Predicate<? super Item> predicate, int amount) {
        if (!isOpen()) {
            return false;
        }
        Item item = Predicates.firstMatching(predicate, getInventory(x -> true));
        if (item != null) {
            if (amount == 28 && !item.isStackable()) {
                return depositAll(item.getId());
            } else if (item.containsAction("Deposit-" + amount)) {
                return item.interact("Deposit-" + amount);
            } else if (item.interact("Deposit-X") && Time.sleepUntil(EnterInput::isOpen, 1500)) {
                Time.sleep(200);
                Keyboard.sendText(String.valueOf(amount));
                Keyboard.pressEnter();
                return true;
            }
        }
        return false;
    }

    /**
     * Makes a call to {@link Bank#deposit(Predicate, int)} by passing a {@code new IdPredicate<>(id)}.
     *
     * @param id     The item id
     * @param amount The amount that needs to be deposited
     * @return The result of {@link Bank#deposit(Predicate, int)}
     * @see IdPredicate
     */
    public static boolean deposit(int id, int amount) {
        return deposit(new IdPredicate<>(id), amount);
    }

    /**
     * Makes a call to {@link Bank#deposit(Predicate, int)} by passing a {@code new NamePredicate<>(id)}.
     *
     * @param name   The item name
     * @param amount The amount that needs to be deposited
     * @return The result of {@link Bank#deposit(Predicate, int)}
     * @see NamePredicate
     */
    public static boolean deposit(String name, int amount) {
        return deposit(new NamePredicate<>(name), amount);
    }

    /**
     * Uses {@link Bank#getFirst(Predicate)} to determine if there is any item in the bank that
     * matches the passed predicate.
     *
     * @param predicate The predicate which the bank item needs to match.
     * @return {@code true} if the bank contains the item.
     */
    public static boolean contains(Predicate<? super Item> predicate) {
        return getFirst(x -> x.getStackSize() > 0 && predicate.test(x)) != null;
    }

    public static boolean contains(Pattern pattern) {
        return contains(e -> e.getName().matches(pattern.pattern()));
    }

    /**
     * Uses {@link Bank#contains(Predicate)} with a {@code new IdPredicate<>(ids)}
     * to determine if there is any item in the bank that matches the ids passed.
     *
     * @param ids The ids which the bank items need to match.
     * @return {@code true} if there is any bank item that matches the passed ids.
     */
    public static boolean contains(int... ids) {
        return contains(new IdPredicate<>(ids));
    }

    /**
     * Uses {@link Bank#contains(Predicate)} with a {@code new NamePredicate<>(names)}
     * to determine if there is any item in the bank that matches the names passed.
     *
     * @param names The names which the bank items need to match.
     * @return {@code true} if there is any bank item that matches the passed names.
     */
    public static boolean contains(String... names) {
        return contains(new NamePredicate<>(names));
    }

    /**
     * Uses {@link Bank#contains(int...)} to determine if the bank does not contain
     * any of the passed ids. If there is any id that is not present in the bank the
     * method will return false.
     *
     * @param ids The ids which the bank items need to match.
     * @return {@code true} if the bank contains all of the passed ids.
     */
    public static boolean containsAll(int... ids) {
        for (int id : ids) {
            if (!contains(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Uses {@link Bank#contains(String...)} to determine if the bank does not contain
     * any of the passed names. If there is any name that is not present in the bank the
     * method will return false.
     *
     * @param names The names which the bank items need to match.
     * @return {@code true} if the bank contains all of the passed names.
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
     * @param names The names of the items to deposit
     */
    public static boolean depositAll(String... names) {
        return depositAll(new NamePredicate<>(names));
    }

    /**
     * @param ids The ids of the items to deposit
     */
    public static boolean depositAll(int... ids) {
        return depositAll(new IdPredicate<>(ids));
    }

    /**
     * @return The current bank {@link Bank.WithdrawMode} state
     */
    public static WithdrawMode getWithdrawMode() {
        return BIT_WITHDRAW_MODE.booleanValue() ? WithdrawMode.NOTE : WithdrawMode.ITEM;
    }

    /**
     * Will set the withdraw mode to the passed value. If the current
     * {@link WithdrawMode} is already set to the passed one, no interaction
     * will be performed.
     *
     * @param mode The {@link WithdrawMode} the bank should be set to
     * @return {@code true} if the interaction was successful.
     */
    public static boolean setWithdrawMode(WithdrawMode mode) {
        if (getWithdrawMode() == mode) {
            return true;
        } else if (!isOpen()) {
            return false;
        }
        InterfaceComponent cmp = Interfaces.lookup(mode.getAddress());
        return cmp != null && cmp.click();
    }

    /**
     * @return The current bank {@link Bank.RearrangeMode} state
     */
    public static RearrangeMode getRearrangeMode() {
        return BIT_REARRANGE_MODE.booleanValue() ? RearrangeMode.INSERT : RearrangeMode.SWAP;
    }

    /**
     * Will set the rearrange mode to the passed value. If the current
     * {@link RearrangeMode} is already set to the passed one, no interaction
     * will be performed.
     *
     * @param mode The {@link RearrangeMode} the bank should be set to
     * @return {@code true} if the interaction was successful.
     */
    public static boolean setRearrangeMode(RearrangeMode mode) {
        if (getRearrangeMode() == mode) {
            return true;
        } else if (!isOpen()) {
            return false;
        }
        InterfaceComponent cmp = Interfaces.lookup(mode.getAddress());
        return cmp != null && cmp.interact(x -> true);
    }

    /**
     * @return The total number of items in the bank
     */
    public static int getCount() {
        return getItems().length;
    }

    /**
     * @param ids The ids which need to be counted
     * @return The amount of items (stacksizes included) that match these ids
     */
    public static int getCount(int... ids) {
        return getCount(new IdPredicate<>(ids));
    }

    public static int getCount(Pattern pattern) {
        return getCount(e -> e.getName().matches(pattern.pattern()));
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

    /**
     * @param predicate What the counted items should match.
     * @return The amount of items (stacksizes included) that match this predicate
     */
    public static int getCount(Predicate<? super Item> predicate) {
        int size = 0;

        Item[] items = getItems(predicate);
        for (Item item : items) {
            size += item.getStackSize();
        }

        return size;
    }

    /**
     * @param names The names which need to be counted
     * @return The amount of items (stacksizes included) that match these names
     */
    public static int getCount(String... names) {
        return getCount(new NamePredicate<>(names));
    }

    /**
     * Determines if the bank contains any items. This method requires the bank to
     * be open. One should ensure that the bank is open before evaluating this
     * logic.
     *
     * @return {@code true} if the bank does not contain at least one item
     * @see #getCount()
     * <p>
     * <b>Note: Defaults to false if the bank is closed</b>
     */
    public static boolean isEmpty() {
        return !isClosed() && getCount() == 0;
    }

    /**
     * This method will withdraw all occurrences of an item. This method only performs one action
     * per loop and will therefore only withdraw all occurrences of the first matching item.
     *
     * @param predicate The predicate which the item needs to match.
     * @return {@code true} if the withdrawal was successful.
     */
    public static boolean withdrawAll(Predicate<? super Item> predicate) {
        if (getOpenTab() != Tab.MAIN_TAB) {
            Tab.MAIN_TAB.open();
            return false;
        }
        Item item = Bank.getFirst(x -> x.getStackSize() > 0 && predicate.test(x));
        return item != null && item.interact("Withdraw-all");
    }

    /**
     * Calls {@link Bank#withdrawAll(Predicate)} using a {@code new IdPredicate<>(id)}
     *
     * @param id The id of the item which needs to be withdrawn
     * @return {@code true} if the interaction was successful
     * @see IdPredicate
     */
    public static boolean withdrawAll(int id) {
        return withdrawAll(new IdPredicate<>(id));
    }

    /**
     * Calls {@link Bank#withdrawAll(Predicate)} using a {@code new NamePredicate<>(name)}
     *
     * @param name The name of the item which needs to be withdrawn
     * @return {@code true} if the interaction was successful
     * @see NamePredicate
     */
    public static boolean withdrawAll(String name) {
        return withdrawAll(new NamePredicate<>(name));
    }

    /**
     * This method will call {@link Bank#withdraw(Predicate, int)} using a {@code new IdPredicate<>(id)}
     *
     * @param id     The id of the item to be withdrawn
     * @param amount The amount which needs to be withdrawn.
     * @return {@code true} if the interaction was successful.
     * @see IdPredicate
     */
    public static boolean withdraw(int id, int amount) {
        return withdraw(new IdPredicate<>(id), amount);
    }

    /**
     * This method will call {@link Bank#withdraw(Predicate, int)} using a {@code new NamePredicate<>(name)}
     *
     * @param name   The name of the item to be withdrawn
     * @param amount The amount which needs to be withdrawn.
     * @return {@code true} if the interaction was successful.
     * @see NamePredicate
     */
    public static boolean withdraw(String name, int amount) {
        return withdraw(new NamePredicate<>(name), amount);
    }

    /**
     * This method will withdraw 'amount' of the first item that matches the given predicate.
     * Considering the method will only perform one action per loop, only 'amount' of the first
     * matching item will be withdrawn on the first loop.
     *
     * @param predicate The predicate which is used to pass to {@link Bank#getFirst(Predicate)} to get the
     *                  item that is to be withdrawn.
     * @param amount    The amount which needs to be withdrawn. If the amount does not match any of the menu
     *                  options, Withdraw-X will be used.
     * @return {@code true} if the interaction was successful.
     */
    public static boolean withdraw(Predicate<? super Item> predicate, int amount) {
        if (!isOpen()) {
            return false;
        } else if (getOpenTab() != Tab.MAIN_TAB) {
            Tab.MAIN_TAB.open();
            return false;
        }
        // Item item = getFirst(x -> predicate.test(x) && x.getStackSize() >= amount);
        Item item = getFirst(x -> x.getStackSize() > 0 && predicate.test(x));
        if (item != null) {
            if (amount == 28 && !item.isStackable() && getWithdrawMode() != WithdrawMode.NOTE) {
                return withdrawAll(item.getId());
            } else if (item.containsAction("Withdraw-" + amount)) {
                return item.interact("Withdraw-" + amount);
            } else if (item.interact("Withdraw-X") && Time.sleepUntil(EnterInput::isOpen, 1500)) {
                Time.sleep(200);
                Keyboard.sendText(String.valueOf(amount));
                Keyboard.pressEnter();
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the maximum number of items that can be stored within the bank.
     * This method requires the bank to be open. If the bank was closed when this
     * method is called, a default value of -1 will be returned. The capacity of the
     * bank will always be greater than or equal to the total number of items within
     * the bank. In the case that the total items within the bank is equal to the
     * capacity, the bank is full, and can no longer store any more items. In the
     * case that the total number of items within the bank is less than the
     * capacity, then the number of free spaces that can be used to store a new item
     * is equal to the capacity minus the total number of items within the bank
     * (capacity - item_count).
     * <p>
     * <b>NOTE: This value should not be assumed constant</b>
     *
     * @return The maximum number of items that can be stored within the bank
     * @see #getCount()
     * @see #isEmpty()
     * @see #isFull()
     */
    public static int getCapacity() {
        if (!isOpen()) {
            return -1;
        }

        InterfaceComponent component = BANK_CAPACITY_ADDRESS.resolve();
        return component != null ? Integer.valueOf(component.getText()) : -1;
    }

    /**
     * Determines if the bank has reach it's capacity, and can no longer store any
     * more-new-items. This method requires the bank to be open. One should ensure
     * that the bank is open before evaluating this logic.
     *
     * @return {@code true} if the bank is full, and can no longer store any
     * more-new-items. Defaults to {@code false} if the bank is closed
     */
    public static boolean isFull() {
        return !isClosed() && getCount() == getCapacity();
    }

    /**
     * Determines the maximum number of -new- items that can be stored within the
     * bank. This method requires the bank to be open. If the bank is closed when
     * this method is called, a default value of -1 will be returned. Any -valid-
     * returned value will be less than or equal to the capacity of the bank. In the
     * case that the a -valid- return value is equal to the capacity of the bank,
     * then the bank is empty. In the case that a -valid- return value is equal to 0
     * the bank is full.
     *
     * @return The maximum number of new items that can be stored within the bank,
     * if the bank is open or -1 if the bank is closed.
     * @see #getCount()
     * @see #getCapacity()
     */
    public static int getFreeSpace() {
        return Bank.isClosed() ? -1 : getCapacity() - getCount();
    }

    /**
     * @param num The number to check for
     * @return {@code true} if there is enough free space to store x items
     */
    public static boolean canStoreXItems(int num) {
        return getFreeSpace() >= num;
    }

    /**
     * @return {@code true} if the main tab panel is not displayed
     */
    public static boolean isSettingsOpen() {
        if (isClosed()) {
            return false;
        }
        InterfaceComponent panel = Interfaces.getComponent(GROUP, 10);
        return panel != null && panel.isExplicitlyHidden();
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> Arrays.asList(getItems()));
    }

    /**
     * The possible 'TabDisplay' options of the bank. This controls the displayed
     * image on all of the tabs dividers
     */
    public enum TabDisplay {
        // Order is with respect of the corresponding varp value, do not change.
        /**
         * The first item of the tab is displayed within the divider of the tab *
         */
        FIRST_ITEM,
        /**
         * The tab index is displayed within the divider of the tab *
         */
        DIGIT,
        /**
         * The tab index in roman numerals is displayed within the divider of the tab *
         */
        ROMAN
    }

    public enum WithdrawMode {

        ITEM(new InterfaceAddress(() -> Interfaces.getFirst(GROUP, x -> x.containsAction("Item")))),
        NOTE(new InterfaceAddress(() -> Interfaces.getFirst(GROUP, x -> x.containsAction("Note"))));

        private final InterfaceAddress address;

        WithdrawMode(InterfaceAddress address) {
            this.address = address;
        }

        public InterfaceAddress getAddress() {
            return address;
        }
    }

    public enum RearrangeMode {

        SWAP(new InterfaceAddress(() -> Interfaces.getFirst(GROUP, x -> x.containsAction("Swap")))),
        INSERT(new InterfaceAddress(() -> Interfaces.getFirst(GROUP, x -> x.containsAction("Insert"))));

        private final InterfaceAddress address;

        RearrangeMode(InterfaceAddress address) {
            this.address = address;
        }

        public InterfaceAddress getAddress() {
            return address;
        }
    }

    /**
     * Pointers for the possible bank tabs. Ordered in respect to the varp value,
     * should not be modified
     */
    public enum Tab {

        MAIN_TAB(null), // tab 0 is not treated as a "tab" in the same way as the others, has no varpbit
        TAB_1(null),
        TAB_2(null),
        TAB_3(null),
        TAB_4(null),
        TAB_5(null),
        TAB_6(null),
        TAB_7(null),
        TAB_8(null),
        TAB_9(null);

        private Varpbit varpbit;

        Tab(Varpbit varpbit) {
            this.varpbit = varpbit;
        }

        public static Tab get(int tab) {
            return Tab.values()[tab];
        }

        public static Tab getOpen() {
            for (Tab tab : Tab.values()) {
                if (tab.isOpen()) {
                    return tab;
                }
            }
            return null;
        }

        /**
         * @return The total number of items within this tab.
         */
        public int getCount() {
            if (this != MAIN_TAB) {
                if (varpbit == null) {
                    varpbit = Varps.getBit(4170 + ordinal());
                }
                return varpbit.getValue();
            }

            // Main Tab is special case and is equal to
            // the total items of the bank minus the sum
            // of the other 9 tabs.
            int sum = 0;
            for (Tab tab : MINOR_TABS) {
                sum += tab.getCount();
            }
            return Bank.getCount() - sum;
        }

        /**
         * Determines if this current tab is in focus. This function requires the bank
         * to be open. If the bank is closed then this function will return false.
         *
         * @return {@code true} if this bank tab is in focus, and the bank is open.
         * @see #getOpenTabIndex()
         */
        public boolean isOpen() {
            return !Bank.isClosed() && Bank.getOpenTabIndex() == getIndex();
        }

        /**
         * Determines if this tab is closed and thus does not have focus. The logic is
         * if this tab is not open, then its closed.
         *
         * @return {@code true} if this bank tab is not open.
         * @see Bank#isOpen
         */
        public boolean isClosed() {
            return !isOpen();
        }

        /**
         * @return The tab number of this tab
         */
        public int getIndex() {
            return ordinal();
        }

        /**
         * Determines if this tab exists, meaning it's currently in use, and occupies at
         * least one slot (not empty). The Main Tab is a special case to this logic, for
         * it can never be collapsed.
         *
         * @return {@code true} if the tab exists
         */
        public boolean isCollapsed() {
            return this != MAIN_TAB && getCount() == 0;
        }

        /**
         * Determines if this tab contains no items. This function performs the same
         * logic as {@link #isCollapsed()} but will include the main tab, since the main
         * tab can also be empty.
         *
         * @return {@code true} if the tab does not contain any items,
         */
        public boolean isEmpty() {
            return getCount() == 0;
        }

        /**
         * The Bank container index base for this tab. All indexes of items within this
         * tab will range between [ BaseValue, BaseValue + ItemCount ).
         * <p>
         * Example: Item 5 of Tab 2 is located within the base value of Tab 2 + 5. Where
         * the resulting index is equal to the index within the Banks item container
         * <p>
         * This value is equal to the sum of the precessing tab count. (T1c + T2c + T3c
         * + ... + Tc(I-1))
         * <p>
         * The MainTab is special case for this logic, for it's items are stored at the
         * end of container, despite it being the 0'th tab (internally).
         *
         * @return The base index of the items for this tab
         */
        public int getContainerBaseIndex() {
            // MainTab is located at the end of the container,
            // though its index is the 0'th...
            if (this == MAIN_TAB) { // Special case
                return Bank.getCount() - this.getCount();
            }
            // Summation
            int count = 0;
            for (Tab tab : MINOR_TABS) { // Ensure 1 -> 9 Order
                if (this == tab) {
                    break;
                }
                count += tab.getCount();
            }
            return count;

        }

        /**
         * Returns the dividing widget of this tab within the tab bar of the bank. This
         * widget displays the first item within the tab, and is what you would use to
         * open this tab. It shall have a valid item id and quantity equal to that of
         * the first item within the tab. It currently proves that despite the display
         * mode of the tab, the first item within the tab [ID+Quantity] is still set.
         *
         * @return The dividing widget header of this tab.
         * @see #getTabDisplay
         */
        public InterfaceComponent getTab() {
            return Interfaces.getComponent(GROUP, 9, 10 + getIndex());
        }

        /**z
         * @return The divider located at the bottom of this tab
         */
        public InterfaceComponent getDivider() {
            if (Bank.isClosed() || !isMainTabOpen() || isCollapsed()) {
                return null;
            }
            // ... The divider should now be active
            int baseIdx = getCapacity() + getIndex();
            InterfaceComponent component = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            return component != null ? component.getComponent(baseIdx) : null;
        }

        public InterfaceComponent getRemote() { // When searching the widget to open the respected tab
            if (Bank.isClosed()) {
                return null;
            }
            int baseIdx = getCapacity() + NUM_TABS + getIndex() - 1;
            InterfaceComponent component = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            return component != null ? component.getComponent(baseIdx) : null;
        }

        /**
         * @return The empty region at the end of each tab where you can drop an item to
         * add to the tab. This region is only updated if their is a gap/space
         * of items at the end of the tab (lower,right).
         */
        public InterfaceComponent getDropRegion() {
            int baseIdx = getCapacity() + NUM_TABS * 2 + getIndex() - 1;
            InterfaceComponent component = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            return component != null ? component.getComponent(baseIdx) : null;
        }

        /**
         * Calculates the container index of a specific relative index, relative to this
         * tab. The returned value can be used to define the index within the banks item
         * container where the specified item is located within the container widget.
         * This index can also define the child index within the container widget where
         * the widget child is indexed.
         * <p>
         * For clarification purposes the provided relative index is forced to be within
         * the item range of the tab, unless the returned value will be that of a
         * different tab. If the relative index is out of range, then -1 will be
         * returned as the returned value. The relative index must be greater than or
         * equal zero and less than the total number of item within the tab in order to
         * be within the range of items within this tab (0 <= i < {@link #getCount()}).
         * The returned value will be the index within the banks item containers which
         * you can lookup the specified item.
         *
         * @param relativeIndex The index of the item, within this tab which must be (0
         *                      <= 0 < {@link #getCount()})
         * @return The index of the item within the container widget, that the relative
         * item (within this tab) is located.
         * @see #getContainerBaseIndex
         */
        public int getItemIndex(int relativeIndex) {
            if (relativeIndex < 0 || relativeIndex > getCount()) {
                return -1;
            }
            int base = getContainerBaseIndex();
            return base + relativeIndex;
        }

        /**
         * Opens this tab. This function will return true if and only if this tab was
         * successfully opened. This function will interact with the divider of the tab
         * provided by {@link #getTab()}.
         * <p>
         * This methods requires this tab to be interactable. In the cases that this tab
         * can not be interacted upon, or this tab is collapsed, this function will
         * immediately return false. In the case that the tab is already open this
         * function will immediately return true.
         *
         * @return {@code true} if and only if this tab was successful opened, or was
         * already open.
         * @see Bank#isOpen
         * @see Tab#isCollapsed
         * @see Tab#getTab
         */
        public boolean open() {
            if (isOpen()) {
                return true;
            } else if (isCollapsed()) {
                return false; // Can't open a collapsed tab
            }

            // Now we can try an open the tab...
            InterfaceComponent divider = getTab();
            if (divider == null) {
                return false;
            }
            if (divider.interact(this == MAIN_TAB ? "View all items" : "View tab")) {
                Time.sleep(500, 800);
                return true;
            }
            // ^ Though pending the event dispatched by the divider is what
            // we're polling for, the events are dispatched within the
            // same thread as the game engine; ether way will work.
            // -----------------------------------------------------------
            // ^ The open tab varpbit is set internally by a rs-event, no
            // need to poll a server response to ensure a valid interaction
            return false;
        }

        public Item getItem(int relativeIndex) {
            int idx = getItemIndex(relativeIndex);
            if (idx == -1) {
                return null;
            }
            InterfaceComponent component = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            if (component == null) {
                return null;
            }
            component = component.getComponent(idx);
            return component != null ? new Item(component) : null;
        }

        public int[] getItemIds(int[] dest, int pos, int length) {
            InterfaceComponent container = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            if (container == null) {
                return new int[0];
            }
            int count = getCount();
            int base = getContainerBaseIndex();
            RSInterfaceComponent[] items = container.getProvider().getComponents();
            int lim = base + count;
            for (int i = base, k = 0; k < length && i < lim; i++) {
                dest[pos + k++] = items[i].getItemId();
            }
            return dest;
        }

        public int[] getItemQuantities(int[] dest, int pos, int length) {
            InterfaceComponent container = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            if (container == null) {
                return new int[0];
            }
            int count = getCount();
            int base = getContainerBaseIndex();
            RSInterfaceComponent[] items = container.getProvider().getComponents();
            int lim = base + count;
            for (int i = base, k = 0; k < length && i < lim; i++) {
                dest[pos + k++] = items[i].getItemStackSize();
            }
            return dest;
        }

        public Item[] getItems() {
            InterfaceComponent container = Interfaces.lookup(SLOT_CONTAINER_ADDRESS);
            if (container == null) {
                return new Item[0];
            }
            int count = getCount();
            int base = getContainerBaseIndex();
            int lim = base + count;
            Item[] dest = new Item[count];
            for (int i = base, k = 0; i < lim; i++) {
                dest[k++] = new Item(container.getComponents()[i]);
            }
            return dest;
        }

        public int[] getItemIds() {
            int count = getCount();
            int[] dest = new int[count];
            return getItemIds(dest, 0, count);
        }

        public int[] getItemQuantities() {
            int count = getCount();
            int[] dest = new int[count];
            return getItemQuantities(dest, 0, count);
        }

        public String toString() {
            return name() + "(Open=" + isOpen() + ",Count=" + getCount() + ",Collapsed=" + isCollapsed() + ",Base="
                    + getContainerBaseIndex() + ")";
        }
    }
}
