package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.providers.annotations.Synthetic;

public interface RSGrandExchangeOffer extends RSProvider {

    int OFFER_INDEX_BASE = 7;
    int OFFER_ACTION_INDEX = 2;
    int OFFER_COLLECT_CONTAINER = 23;

    int PROGRESS_MASK = 2;
    int FINISH_MASK = 4;
    int SELLING_MASK = 8;

    byte getState();

    int getItemId();

    default String getItemName() {
        RSItemDefinition definition = getItemDefinition();
        return definition == null || definition.getName() == null ? "" : definition.getName();
    }

    int getItemPrice();

    int getItemQuantity();

    int getSpent();

    int getTransferred();

    @Synthetic
    int getIndex();

    default boolean isEmpty() {
        return getState() == 0;
    }

    default Type getType() {
        byte state = getState();
        if (state == 0) {
            return Type.EMPTY;
        } else if ((getState() & SELLING_MASK) == SELLING_MASK) {
            return Type.SELL;
        }
        return Type.BUY;
    }

    default boolean create(Type type) {
        Type current = getType();
        if (current != Type.EMPTY) {
            return false;
        }
        InterfaceComponent root = getOfferComponent();
        if (root == null) {
            return false;
        }
        InterfaceComponent child = root.getComponent(comp -> comp.containsAction(
                action -> action.toLowerCase().contains(type.name().toLowerCase()))
        );
        return child != null && child.interact(e -> true);
    }

    default RSItemDefinition getItemDefinition() {
        return Definitions.getItem(getItemId());
    }

    default boolean abort() {
        InterfaceComponent actionChild = getActionSubcomponent();
        return actionChild != null && actionChild.interact("Abort offer");
    }

    default boolean view() {
        InterfaceComponent actionChild = getActionSubcomponent();
        return actionChild != null && actionChild.interact("View offer");
    }

    default boolean collect(CollectionAction action) {
        InterfaceComponent container = Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), OFFER_COLLECT_CONTAINER);
        if (container == null) {
            return false;
        }

        boolean success = true;
        for (InterfaceComponent cmp : container.getComponents()) {
            if (cmp.getItemId() != -1 && cmp.containsAction("Bank")) {
                success &= cmp.interact(cmp.containsAction(action.text)
                        ? x -> x.equals(action.text)
                        : x -> x.contains("Collect"));
                Time.sleep(Random.mid(60, 110));
            }
        }
        return success;
    }

    default InterfaceComponent getActionSubcomponent() {
        InterfaceComponent component = getOfferComponent();
        if (component == null) {
            return null;
        }

        return component.getComponent(OFFER_ACTION_INDEX);
    }

    default InterfaceComponent getOfferComponent() {
        return Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), OFFER_INDEX_BASE + getIndex());
    }

    default Progress getProgress() {
        byte state = getState();
        if ((state & PROGRESS_MASK) == PROGRESS_MASK) {
            return Progress.IN_PROGRESS;
        } else if ((state & FINISH_MASK) == FINISH_MASK) {
            return Progress.FINISHED;
        }
        return Progress.EMPTY;
    }

    enum Type {
        BUY, SELL, EMPTY
    }

    enum Progress {
        EMPTY, IN_PROGRESS, FINISHED
    }

    enum CollectionAction {

        NOTE("Collect-notes"),
        ITEM("Collect-items"),
        BANK("Bank");

        private final String text;

        CollectionAction(String text) {
            this.text = text;
        }
    }
}