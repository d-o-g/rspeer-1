package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.query.GrandExchangeOfferQueryBuilder;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @see GrandExchangeSetup
 */
public final class GrandExchange {

    private static final int INTERFACE_INDEX = InterfaceComposite.GRAND_EXCHANGE.getGroup();
    private static final int INVENTORY_INDEX = InterfaceComposite.GRAND_EXCHANGE_INVENTORY.getGroup();

    private static final int INVENTORY_CONTAINER = 0;

    private static final int OFFER_SETUP_CONTAINER = 24;
    private static final int SET_QUANTITY_COMPONENT = 7;
    private static final int LOWER_PRICE_COMPONENT = 10;
    private static final int UPPER_PRICE_COMPONENT = 13;
    private static final int SET_PRICE_COMPONENT = 12;
    private static final int TEXT_COMPONENT = 18;
    private static final int SELECTED_ITEM_COMPONENT = 21;
    private static final int QUANTITY_TEXT_COMPONENT = 32;

    private static final int BACK_COMPONENT = 4;
    private static final int CONFIRM_COMPONENT = 27;

    private static final int BUY_ITEM_QUERY_SCRIPT = 754;

    private static final int VIEW_BIT = 4439; //0 on overview, 3 on buy/sell
    private static final int TYPE_BIT = 4397; //0 on buy 1 on sell

    private GrandExchange() {
        throw new IllegalAccessError();
    }

    public static RSGrandExchangeOffer[] getOffers() {
        RSGrandExchangeOffer[] offers = Game.getClient().getGrandExchangeOffers();
        return offers != null ? offers : new RSGrandExchangeOffer[0];
    }

    public static RSGrandExchangeOffer[] getOffers(Predicate<? super RSGrandExchangeOffer> predicate) {
        List<RSGrandExchangeOffer> offers = new ArrayList<>();
        RSGrandExchangeOffer[] all = getOffers();
        for (RSGrandExchangeOffer offer : all) {
            if (predicate.test(offer)) {
                offers.add(offer);
            }
        }
        return offers.toArray(new RSGrandExchangeOffer[0]);
    }

    public static RSGrandExchangeOffer getFirst(Predicate<? super RSGrandExchangeOffer> predicate) {
        RSGrandExchangeOffer[] offers = getOffers(predicate);
        return offers.length > 0 ? offers[0] : null;
    }

    public static RSGrandExchangeOffer[] getOffers(RSGrandExchangeOffer.Type type) {
        return getOffers(offer -> offer.getType() == type);
    }

    public static RSGrandExchangeOffer getFirstActive() {
        return getFirst(x -> x.getType() != RSGrandExchangeOffer.Type.EMPTY);
    }

    public static RSGrandExchangeOffer getFirstEmpty() {
        return getFirst(x -> {
            if (x.getType() != RSGrandExchangeOffer.Type.EMPTY) {
                return false;
            }
            InterfaceComponent btn = Container.getBuyButton(x.getIndex());
            return btn != null && btn.containsAction(y -> y.contains("offer"));
        });
    }

    /**
     * @return {@code true} if the main grand exchange interface is open. It is
     * important to note that this does not return true for the grand
     * exchange history or sets interfaces
     */
    public static boolean isOpen() {
        return Interfaces.isOpen(INTERFACE_INDEX);
    }

    /**
     * Creates an offer with the given type
     *
     * @param type The offer type
     * @return {@code true} if successfully created an offer
     */
    public static boolean createOffer(RSGrandExchangeOffer.Type type) {
        RSGrandExchangeOffer empty = getFirstEmpty();
        return empty != null && empty.create(type);
    }

    public static InterfaceComponent getInventory() {
        return Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE_INVENTORY.getGroup(), 0);
    }

    public static View getView() {
        InterfaceComponent component = Interfaces.getComponent(INTERFACE_INDEX, OFFER_SETUP_CONTAINER);
        if (component != null) {
            int view = Varps.getBitValue(VIEW_BIT);
            if (view == 0) {
                return View.OVERVIEW;
            }

            int type = Varps.getBitValue(TYPE_BIT);
            return type == 0 ? View.BUY_OFFER : View.SELL_OFFER;
        }
        return View.CLOSED;
    }

    private static int getOpenIndex() {
        int index = -1;
        RSGrandExchangeOffer[] offers = getOffers();
        for (int i = 0; i < offers.length; i++) {
            RSGrandExchangeOffer offer = offers[i];
            if (offer.isEmpty()) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean open() {
        Position p = BankLocation.GRAND_EXCHANGE.getPosition();
        if (p.distance() >= 10) {// || !Movement.isInteractable(p, false)) {
            if (Movement.walkTo(p)) {
                Time.sleep(250, 550);
            }
            return false;
        }

        Npc clerk = Npcs.getNearest("Grand Exchange Clerk");
        return clerk != null && clerk.interact("Exchange") && Time.sleepUntil(GrandExchange::isOpen, 2400);
    }


    public static boolean open(View view) {
        if (!isOpen()) {
            open();
            return false;
        }
        View current = getView();
        if (current == view) {
            return true;
        } else if (view == View.CLOSED) {
            Keyboard.pressEventKey(KeyEvent.VK_ESCAPE);
            return Time.sleepUntil(() -> !isOpen(), 600);
        } else if (view == View.OVERVIEW) {
            InterfaceComponent back = Interfaces.getComponent(INTERFACE_INDEX, BACK_COMPONENT);
            return back != null && back.interact(x -> true);
        } else if (view == View.BUY_OFFER || view == View.SELL_OFFER) {
            if (current != View.OVERVIEW) {
                open(View.OVERVIEW);
                return false;
            }
            int open = getOpenIndex();
            if (open == -1) {
                return false; // no free slots
            }
            InterfaceComponent button = view == View.BUY_OFFER
                    ? Container.getBuyButton(open)
                    : Container.getSellButton(open);
            return button != null && button.interact(x -> true);
        }
        return false;
    }

    /**
     * @param toBank {@code true} to collect to the bank, {@code false} to collect to
     *               the inventory
     * @return Collects all items to the specified interface
     */
    public static boolean collectAll(boolean toBank) {
        InterfaceComponent collect = Interfaces.getComponent(465, 6, 0);
        if (collect != null && collect.isVisible()) {
            String action = "Collect to " + (toBank ? "bank" : "inventory");
            return collect.interact(action);
        }
        return false;
    }

    /**
     * @return Collects all items to the inventory
     */
    public static boolean collectAll() {
        return collectAll(false);
    }

    public static short[] getSearchResults() {
        InterfaceComponent component = Interfaces.getComponent(162, 37);
        if (component != null && component.isVisible()) {
            short[] ids = Game.getClient().getGrandExchangeSearchResults();
            if (ids != null) {
                return ids;
            }
        }
        return new short[0];
    }

    public static GrandExchangeOfferQueryBuilder newQuery() {
        return new GrandExchangeOfferQueryBuilder();
    }

    public enum View {
        OVERVIEW, BUY_OFFER, SELL_OFFER, CLOSED;
    }

    private static class Container {

        private static final int FIRST_CONTAINER = 7;

        private static final int SUB_VIEW_OFFER = 2;
        private static final int SUB_BUY_BUTTON = 3;
        private static final int SUB_SELL_BUTTON = 4;

        private static InterfaceComponent get(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_CONTAINER + index);
        }

        private static RSGrandExchangeOffer getOffer(int index) {
            try {
                return getOffers()[index];
            } catch (Exception ignored) {
                return null;
            }
        }

        private static InterfaceComponent getBuyButton(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_CONTAINER + index, SUB_BUY_BUTTON);
        }

        private static InterfaceComponent getSellButton(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_CONTAINER + index, SUB_SELL_BUTTON);
        }

        private static InterfaceComponent getViewOfferButton(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_CONTAINER + index, SUB_VIEW_OFFER);
        }
    }

   /* public static class Price {

        private static final Map<String, int[]> ID_BY_NAME
                = Collections.synchronizedMap(new HashMap<>());

        private static final Gson GSON = new Gson();
        private static final Map<Integer, Item> PRICES = new HashMap<>();
        private static int reloadMinutes = 30;
        private static boolean isReloadEnabled = true;

        public static Item lookup(String name, Predicate<RSItemDefinition> predicate) {
            int[] ids = ID_BY_NAME.get(name);
            if (ids == null || ids.length == 0) {
                return null;
            }

            int count = ids.length;
            RSItemDefinition[] defs = new RSItemDefinition[count];
            for (int i = 0; i < count; i++) {
                defs[i] = Definitions.getItem(ids[i]);
            }

            RSItemDefinition definition = Predicates.firstMatching(x -> !x.isNoted() && predicate.test(x), defs);
            return definition != null ? lookup(definition.getId()) : null;
        }

        public static Item lookup(String name) {
            int[] ids = ID_BY_NAME.get(name);
            return ids != null && ids.length > 0 ? lookup(ids[0]) : null;
        }

        public static Item lookup(int id) {
            return null;
        }

        public enum Source {
            RSBUDDY,
            RUNESCAPE
        }

        public class Item implements Identifiable {

            private final int id;
            private final RSItemDefinition definition;

            private Item(int id) {
                this.id = id;
                definition = Definitions.getItem(id);
            }

            public RSItemDefinition getDefinition() {
                return definition;
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getName() {
                return definition != null ? definition.getName() : "";
            }
        }
    }*/
}
