package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;

import java.util.function.Predicate;

public final class GrandExchangeSetup {

    private static final int SETUP_OFFER_INDEX = 24;
    private static final int OFFER_TYPE_INDEX = 18;

    private static final int ITEM_INDEX = 21;

    private static final int QUANTITY_TEXT_INDEX = 32;
    private static final int SET_QUANTITY_INDEX = 7;

    private static final int PRICE_TEXT_INDEX = 39;
    private static final int SET_PRICE_INDEX = 12;
    private static final int DECREASE_PRICE_INDEX = 10;
    private static final int INCREASE_PRICE_INDEX = 13;

    private static final int CONFIRM_INDEX = 27;

    private static final int SELECT_SCRIPT_ID = 754;
    private static final int SELECT_ARG = 84;

    private GrandExchangeSetup() {
        throw new IllegalAccessError();
    }

    /**
     * @return The type of the current offer being set up.
     * If no offer is being set up, the default return is {@link RSGrandExchangeOffer.Type#EMPTY}
     */
    public static RSGrandExchangeOffer.Type getSetupType() {
        InterfaceComponent text = getSetupChild(OFFER_TYPE_INDEX);
        if (text == null) {
            return RSGrandExchangeOffer.Type.EMPTY;
        }

        return RSGrandExchangeOffer.Type.valueOf(text.getText().split(" ")[0].toUpperCase());
    }

    private static InterfaceComponent getSetupComponent() {
        return Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), SETUP_OFFER_INDEX);
    }

    private static InterfaceComponent getSetupChild(int index) {
        InterfaceComponent setup = getSetupComponent();
        if (setup == null) {
            return null;
        }

        return setup.getComponent(index);
    }

    /**
     * @return The quantity of the current item in the setup screen
     */
    public static int getQuantity() {
        return getTextNumber(QUANTITY_TEXT_INDEX);
    }

    /**
     * @return The price of the current item in the setup screen
     */
    public static int getPricePerItem() {
        return getTextNumber(PRICE_TEXT_INDEX);
    }

    private static int getTextNumber(int index) {
        InterfaceComponent quantity = getSetupChild(index);
        if (quantity == null) {
            return -1;
        }
        String text = quantity.getText()
                .replace(",", "")
                .replace("coins", "")
                .replace("coin", "").trim();
        return text.isEmpty() ? -1 : Integer.parseInt(text);
    }

    /**
     * @return The current item in the setup screen
     */
    public static Item getItem() {
        InterfaceComponent child = getSetupChild(ITEM_INDEX);
        return child != null && child.getItemId() != 6512 ? new Item(child) : null;
    }

    /**
     * @return The id of the current item in the setup screen
     * @Deprecated See {@link #getItem()}
     */
    @Deprecated
    public static int getItemId() {
        InterfaceComponent child = getSetupChild(ITEM_INDEX);
        int id = child == null ? -1 : child.getItemId();
        return id == 6512 ? -1 : id; //6512 is also empty item, applies to bank too
    }

    /**
     * @return {@code true} if the setup screen is open
     */
    public static boolean isOpen() {
        InterfaceComponent validate = Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), SETUP_OFFER_INDEX);
        return validate != null && validate.isVisible();
    }

    /**
     * @param id The id of the desired item
     * @return Attempts to offer an item to place in the exchange
     * offer screen, returns {@code true} on successful interaction
     */
    public static boolean setItem(int id) {
        RSGrandExchangeOffer.Type type = getSetupType();
        if (type == RSGrandExchangeOffer.Type.BUY) {
            Game.getClient().fireScriptEvent(SELECT_SCRIPT_ID, id, SELECT_ARG);
            return true;
        }
        InterfaceComponent geInventory = GrandExchange.getInventory();
        if (geInventory == null) {
            return false;
        }
        InterfaceComponent item = geInventory.getComponent(e -> e.getItemId() == id);
        return item != null && item.interact("Offer");
    }

    /**
     * Note: It is recommended to use {@link GrandExchangeSetup#setItem(int)} instead of this
     *
     * @param name The name of the desired item
     * @return Attempts to offer an item to place in the exchange
     * offer screen, returns {@code true} on successful interaction
     */
    public static boolean setItem(String name) {
        if (getSetupType() == RSGrandExchangeOffer.Type.BUY) {
            RSItemDefinition definition = Definitions.getItem(name, e -> e.isTradable() && !e.isNoted());
            return definition != null && setItem(definition.getId());
        } else {
            Item item = Inventory.getFirst(name);
            return item != null && setItem(item.getId());
        }
    }

    /**
     * Note: It is recommended to use {@link GrandExchangeSetup#setItem(int)} instead of this
     *
     * @param name      The name of the desired item
     * @param predicate Due to name collisions, a predicate may be supplied to narrow down
     *                  the search for the item
     * @return Attempts to offer an item to place in the exchange
     * offer screen, returns {@code true} on successful interaction
     */
    public static boolean setItem(String name, Predicate<RSItemDefinition> predicate) {
        RSItemDefinition definition = Definitions.getItem(name,
                ((Predicate<RSItemDefinition>) e -> !e.isNoted() && e.isTradable()).and(predicate));
        return definition != null && setItem(definition.getId());
    }

    /**
     * @param price The price of the item in the offer
     * @return Attempts to change the price of the current item in the offer,
     * returns {@code true} on successful interaction
     */
    public static boolean setPrice(int price) {
        InterfaceComponent setButton = getSetupChild(SET_PRICE_INDEX);
        if (setButton == null) {
            return false;
        }
        return setButton.interact("Enter price")
                && Time.sleepUntilForDuration(EnterInput::isOpen, 850, 2500)
                && EnterInput.initiate(price);
    }

    /**
     * @param quantity The quantity of the item in the offer
     * @return Attempts to change the amount of the current item in the offer,
     * returns {@code true} on successful interaction
     */
    public static boolean setQuantity(int quantity) {
        InterfaceComponent setButton = getSetupChild(SET_QUANTITY_INDEX);
        if (setButton == null) {
            return false;
        }
        return setButton.interact("Enter quantity")
                && Time.sleepUntilForDuration(EnterInput::isOpen, 850, 2500)
                && EnterInput.initiate(quantity);
    }

    /**
     * @param times The number of times to select the increase price button
     * @return Attempts to increase the price of the item by using the +5% button
     * returns {@code true} on successful interaction
     */
    public static boolean increasePrice(int times) {
        InterfaceComponent increaseButton = getSetupChild(INCREASE_PRICE_INDEX);
        if (increaseButton == null) {
            return false;
        }
        boolean result = true;
        for (int i = 0; i < times; i++) {
            result &= increaseButton.interact("+5%");
        }
        return result;
    }

    /**
     * @param times The number of times to select the decrease price button
     * @return Attempts to decrease the price of the item by using the -5% button
     * returns {@code true} on successful interaction
     */
    public static boolean decreasePrice(int times) {
        InterfaceComponent decreaseButton = getSetupChild(DECREASE_PRICE_INDEX);
        if (decreaseButton == null) {
            return false;
        }
        boolean result = true;
        for (int i = 0; i < times; i++) {
            result &= decreaseButton.interact("-5%");
        }
        return result;
    }

    /**
     * @return Selects the confirm button offer and returns {@code true} on successful interaction
     */
    public static boolean confirm() {
        InterfaceComponent confirm = Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), CONFIRM_INDEX);
        return confirm != null && confirm.interact("Confirm");
    }
}