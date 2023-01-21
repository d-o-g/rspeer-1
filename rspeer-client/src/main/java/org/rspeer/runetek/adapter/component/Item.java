package org.rspeer.runetek.adapter.component;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.InterfaceConfig;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.rspeer.runetek.providers.RSSprite;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by MadDev on 11/19/17.
 */
public final class Item implements Identifiable, Interactable {

    private final InterfaceComponent modelComponent;
    private InterfaceComponent interactableComponent;

    private final int index;
    private final int id;
    private final int stackSize;
    private final RSItemDefinition definition;

    public Item(InterfaceComponent modelComponent, InterfaceComponent interactableComponent, int index, int id, int stackSize) {
        if (modelComponent == null) {
            throw new IllegalStateException();
        }
        this.modelComponent = modelComponent;
        this.interactableComponent = interactableComponent;
        this.index = index;
        this.id = id;
        this.stackSize = stackSize;
        definition = Definitions.getItem(id);
    }

    public Item(InterfaceComponent component, int index, int id, int stackSize) {
        this(component, null, index, id, stackSize);
    }

    public Item(InterfaceComponent model, InterfaceComponent interactable, int index) {
        this(model, interactable, index, model.getItemIds()[index] - 1, model.getItemStackSizes()[index]);
    }

    public Item(InterfaceComponent model, InterfaceComponent interactable) {
        this(model, interactable, model.getComponentIndex(), model.getItemId(), model.getItemStackSize());
    }

    /**
     * This constructor should only be used for inventory and other table-layout components.
     * A table layout is an InterfaceComponent where InterfaceComponent.getType() == 2
     *
     * @param component the component contsining the items
     * @param index     the index of the item (slot)
     */
    public Item(InterfaceComponent component, int index) {
        this(component, null, index);
    }

    //for item containers where 1 component = 1 item e.g. bank
    public Item(InterfaceComponent component) {
        this(component, null);
    }

    public static Item[] from(InterfaceComponent comp, Predicate<? super Item> predicate) {
        if (comp.getType() != 2 && comp.getComponentCount() > 0) {
            return fromSubcomponents(comp, predicate);
        }
        return fromTableStructure(comp, predicate);
    }

    private static Item[] fromSubcomponents(InterfaceComponent comp, Predicate<? super Item> predicate) {
        List<Item> items = new ArrayList<>();
        for (InterfaceComponent burak : comp.getComponents()) {
            if (burak.getItemId() != -1) {
                Item endre = new Item(burak);
                if (predicate.test(endre)) {
                    items.add(endre);
                }
            }
        }
        return items.toArray(new Item[0]);
    }

    private static Item[] fromTableStructure(InterfaceComponent comp, Predicate<? super Item> predicate) {
        List<Item> items = new ArrayList<>();
        int[] ids = comp.getItemIds();
        int[] amts = comp.getItemStackSizes();
        if (ids.length != amts.length || ids.length == 0) {
            return new Item[0];
        }
        for (int i = 0; i < ids.length; i++) {
            int id = ids[i] - 1;
            if (id > 0) {
                int amt = amts[i];
                Item item = new Item(comp, i, id, amt);
                if (predicate.test(item)) {
                    items.add(item);
                }
            }
        }
        return items.toArray(new Item[0]);
    }


    /**
     * Only use when one InterfaceComponent contains contain multiple items. For example, inventory widget (149, 0)
     * is a single InterfaceComponent but contains multiple items
     *
     * @param comp the widget
     * @return An array of items
     */
    public static Item[] from(InterfaceComponent comp) {
        return from(comp, t -> true);
    }

    public RSItemDefinition getDefinition() {
        return definition;
    }

    public InterfaceComponent getComponent() {
        return modelComponent;
    }

    public InterfaceComponent getInteractableComponent() {
        return interactableComponent == null ? modelComponent : interactableComponent;
    }

    public int getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }

    public int getStackSize() {
        return stackSize;
    }

    public boolean isStackable() {
        return Functions.mapOrElse(this::getDefinition, RSItemDefinition::isStackable, false);
    }

    public boolean isExchangeable() {
        if (isNoted()) {
            RSItemDefinition unnoted = Definitions.getItem(getUnnotedId());
            if (unnoted != null && unnoted.getName() != null && unnoted.getName().equals(getName())) {
                return unnoted.isTradable();
            }
        }
        return Functions.mapOrElse(this::getDefinition, RSItemDefinition::isTradable, false);
    }

    @Override
    public String getName() {
        return Functions.mapOrDefault(() -> definition, RSItemDefinition::getName, "");
    }

    public Rectangle getBounds() {
        if (modelComponent.getType() == 2) { //type 2 = table layout
            int columns = modelComponent.getWidth();
            // When its a table layout, its width and height are used to store
            // the dimensions of the table.
            int row = index / columns;
            int col = index % columns;
            int paddingX = modelComponent.getXPadding();
            int paddingY = modelComponent.getYPadding();
            int baseX = modelComponent.getX();
            int baseY = modelComponent.getY();
            int x = baseX + ((32 + paddingX) * col);
            int y = baseY + ((32 + paddingY) * row);
            return new Rectangle(x - 1, y - 1, 32, 32);
            // ^ Bounds are translated by one pixel as the client internally recognises >=
            // ^ All bounds have a constant dimension of 32 x 32
        }
        return modelComponent.getBounds(); //the component bounds already represent the item
    }

    public Point getPoint() {
        Rectangle bounds = getBounds();
        return new Point(bounds.x + Random.mid(1, 31), bounds.y + Random.mid(1, 31));
    }

    public void draw(Graphics g) {
        Rectangle bounds = getBounds();
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public String[] getActions() {
        InterfaceComponent component = interactableComponent != null ? interactableComponent : modelComponent;
        String[] arr = component.getProvider().getActions();
        if (component.getType() == 2) {
            if (InterfaceConfig.isUsingActionsFromDefinition(component.getConfig())) {
                arr = definition.getActions();
            } else {
                arr = component.getTableActions();
            }
        }

        if (arr == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : arr) {
            if (action != null) {
                actions.add(StringCommons.replaceColorTag(action));
            }
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public String[] getRawActions() {
        InterfaceComponent component = interactableComponent != null ? interactableComponent : modelComponent;
        if (component.getType() != 2) {
            return component.getActions();
        } else if (InterfaceConfig.isUsingActionsFromDefinition(component.getConfig())) {
            return definition.getActions();
        }
        return component.getTableActions();
    }

    public boolean isNoted() {
        return definition != null && definition.getNoteTemplateId() != -1;
    }

    public int getNotedId() {
        return definition.getNotedId();
    }

    public int getUnnotedId() {
        return definition.getUnnotedId();
    }

    public RSSprite loadSprite() {
        return Game.getClient().getItemSprite(id, stackSize, 1, 0x302020, isStackable() ? 1 : 0, isNoted());
    }
}
