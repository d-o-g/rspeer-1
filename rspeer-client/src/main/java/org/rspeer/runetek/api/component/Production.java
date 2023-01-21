package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.api.commons.predicate.NamePredicate;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.query.ItemQueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Spencer on 12/02/2018.
 */
public final class Production {

    private static final int INTERFACE_INDEX = InterfaceComposite.CREATION.getGroup();
    private static final int NUMBER_SUBCOMPONENT = 9;

    private Production() {
        throw new IllegalAccessError();
    }

    /**
     * @return {@code true} if the production interface is open
     */
    public static boolean isOpen() {
        return Interfaces.isOpen(INTERFACE_INDEX);
    }

    /**
     * @return The current amount that the production interface is set to
     */
    public static Amount getAmount() {
        if (!isOpen()) {
            return null;
        }
        for (Amount mode : Amount.values()) {
            InterfaceComponent button = Interfaces.getComponent(INTERFACE_INDEX, mode.getComponentIndex(), NUMBER_SUBCOMPONENT);
            if (button != null && button.getText().contains("<col")) {
                return mode;
            }
        }
        return Amount.ALL;
    }

    /**
     * Changes the amount to produce
     * @param amount The amount to switch to
     * @return {@code true} if the amount was successfully set to the given amount
     */
    public static boolean setAmount(Amount amount) {
        if (!isOpen()) {
            return false;
        }
        if (getAmount() == amount) {
            return true;
        }
        InterfaceComponent button = Interfaces.getComponent(INTERFACE_INDEX, amount.getComponentIndex());
        return button != null && button.isVisible() && button.interact(x -> true) && getAmount() == amount;
    }

    /**
     * Selects the amount to initiate the production, also switching to it if necessary
     * @param amount The amount to switch to
     * @return
     */
    public static boolean initiate(Amount amount) {
        return getAmount() == amount ? initiate() : setAmount(amount) && initiate();
    }

    /**
     * Initiates the production
     * @param optionIndex the index of the option to choose. For example if the options
     *               are body, vambraces and chaps, 0 = body, 1 = vambraces, 2 = chaps
     * @return {@code true} if the production was successfully initiated
     */
    public static boolean initiate(int optionIndex) {
        if (!isOpen()) {
            return false;
        }
        Keyboard.sendKey((char) ('1' + optionIndex));
        return Time.sleepUntil(() -> !isOpen(), 1000);
    }

    /**
     * Initiates the production
     * @return {@code true} if the production was successfully initiated
     */
    public static boolean initiate() {
        return initiate(0);
    }

    /**
     * @return The current amount, if a custom amount is set or -1
     */
    public static int getCustomAmount() {
        if (!isOpen()) {
            return -1;
        }
        try {
            InterfaceComponent button = Interfaces.getComponent(INTERFACE_INDEX, Amount.CUSTOM.getComponentIndex(), 9);
            if (button != null && button.isVisible()) {
                return Integer.parseInt(button.getText().replace("<col=ffffff>", "").replace("</col>", ""));
            }
        } catch (Exception ignored) {

        }
        return -1;
    }

    /**
     * @return {@code true} if a custom amount is set
     */
    public static boolean isCustomAmountSet() {
        return isOpen() && getCustomAmount() != -1;
    }

    public enum Amount {

        ONE(7, 1),
        FIVE(8, 5),
        TEN(9, 10),
        CUSTOM(10, -1),
        X(11, -1),
        ALL(12, -1);

        private final int component;
        private final int value;

        Amount(int component, int value) {
            this.component = component;
            this.value = value;
        }

        public int getComponentIndex() {
            return component;
        }

        public int getValue() {
            return value == -1 ? getCustomAmount() : value;
        }
    }

    private static Item[] getItems(Predicate<Item> predicate) {
        List<Item> items = new ArrayList<>();
        for (InterfaceComponent component : Interfaces.get(INTERFACE_INDEX, x -> !x.getName().isEmpty() && x.containsAction(y -> y.length() > 0))) {
            InterfaceComponent model = component.getComponent(x -> x.getItemId() != -1);
            if (model != null) {
                Item item = new Item(model);
                if (predicate.test(item)) {
                    items.add(item);
                }
            }
        }
        return items.toArray(new Item[0]);
    }

    private static Item[] getItems() {
        return getItems(Predicates.always());
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> Arrays.asList(getItems()));
    }

    private static Item getFirst(Predicate<Item> predicate) {
        for (InterfaceComponent component : Interfaces.get(INTERFACE_INDEX, x -> !x.getName().isEmpty() && x.containsAction(y -> y.length() > 0))) {
            InterfaceComponent model = component.getComponent(x -> x.getItemId() != -1);
            if (model != null) {
                Item item = new Item(model);
                if (predicate.test(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static boolean initiate(Predicate<Item> predicate) {
        if (!isOpen()) {
            return false;
        }
        Item item = getFirst(predicate);
        if (item != null) {
            InterfaceComponent model = item.getComponent();
            InterfaceComponent actionParent = Interfaces.getComponent(model.getRootIndex(), model.getParentIndex());
            return actionParent != null && actionParent.interact(x -> true);
        }
        return false;
    }

    public static boolean initiate(int... itemIds) {
        return initiate(new IdPredicate<>(itemIds));
    }

    public static boolean initiate(String... itemNames) {
        return initiate(new NamePredicate<>(itemNames));
    }
}
