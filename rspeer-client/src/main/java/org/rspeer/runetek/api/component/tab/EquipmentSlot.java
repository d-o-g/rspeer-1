package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.providers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Man16
 * @author Spencer
 */
public enum EquipmentSlot {

    HEAD(15, 0),
    CAPE(16, 1),
    NECK(17, 2),
    MAINHAND(18, 3),
    CHEST(19, 4),
    OFFHAND(20, 5),
    LEGS(21, 7),
    HANDS(22, 9),
    FEET(23, 10),
    RING(24, 12),
    QUIVER(25, 13);

    private static final int INTERFACE_INDEX = 387;
    private static final int SUBCOMPONENT_INDEX = 1;

    private static final int BASE_ACTION_PARAM = 451;
    private static final int MAX_CUSTOM_ACTIONS = 8;

    private final InterfaceAddress slotAddress;
    private final InterfaceAddress itemAddress;
    private final int tableIndex;

    EquipmentSlot(int componentIndex, int tableIndex) {
        slotAddress = new InterfaceAddress(INTERFACE_INDEX, componentIndex);
        itemAddress = slotAddress.subComponent(SUBCOMPONENT_INDEX);
        this.tableIndex = tableIndex;
    }

    public InterfaceAddress getSlotAddress() {
        return slotAddress;
    }

    public InterfaceAddress getItemAddress() {
        return itemAddress;
    }

    public int getItemId() {
        RSItemTable table = ItemTables.lookup(ItemTables.EQUIPMENT);
        if (table == null) {
            return -1;
        }
        int[] ids = table.getIds();
        return ids == null || ids.length == 0 || tableIndex >= ids.length ? -1 : ids[tableIndex];
    }

    public int getItemStackSize() {
        RSItemTable table = ItemTables.lookup(ItemTables.EQUIPMENT);
        if (table == null) {
            return -1;
        }
        int[] stacks = table.getStackSizes();
        return stacks == null || stacks.length == 0 || tableIndex >= stacks.length ? -1 : stacks[tableIndex];
    }

    public RSItemDefinition getDefinition() {
        int id = getItemId();
        return id != -1 ? Definitions.getItem(id) : null;
    }

    public Item getItem() {
        int id = getItemId();
        if (id == -1) {
            return null;
        }
        InterfaceComponent component = getItemComponent();
        return component != null ? new Item(component, component.getComponentIndex(), getItemId(), getItemStackSize()) : null;
    }

    public String getItemName() {
        RSItemDefinition def = getDefinition();
        return def != null && def.getName() != null ? def.getName() : "";
    }

    public InterfaceComponent getComponent() {
        return Interfaces.lookup(slotAddress);
    }

    public InterfaceComponent getItemComponent() {
        return Interfaces.lookup(itemAddress);
    }

    public String[] getActions() {
        RSRS3CopiedNodeTable<? extends RSNode> table = Functions.mapOrNull(this::getDefinition, RSItemDefinition::getProperties);
        if (table == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        actions.add("Remove");

        for (int param = BASE_ACTION_PARAM; param < (BASE_ACTION_PARAM + MAX_CUSTOM_ACTIONS) - 1; param++) {
            RSNode node = table.safeLookup(param);
            if (node instanceof RSObjectNode) {
                RSObjectNode obj = (RSObjectNode) node;
                if (obj.getValue() instanceof String) {
                    actions.add(StringCommons.replaceColorTag((String) obj.getValue()));
                }
            }
        }

        actions.add("Examine");
        return actions.toArray(new String[0]);
    }

    private int getActionIndex(Predicate<String> action) {
        String[] actions = getActions();
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] != null && action.test(actions[i])) {
                return i;
            }
        }
        return -1;
    }

    private int getActionIndex(String action) {
        return getActionIndex(x -> x.equalsIgnoreCase(action));
    }

    public boolean interact(String action) {
        int index = getActionIndex(action);
        return index >= 0 && interact(ActionOpcodes.INTERFACE_ACTION, index);
    }

    public boolean interact(Predicate<String> predicate) {
        int index = getActionIndex(predicate);
        return index >= 0 && interact(ActionOpcodes.INTERFACE_ACTION, index);
    }

    /**
     * @see EquipmentSlot#interact(int, int)
     */
    public boolean interact(int opcode) {
        InterfaceComponent component = getComponent();
        return component != null && component.interact(opcode);
    }

    public boolean interact(int opcode, int actionIndex) {
        InterfaceComponent component = getComponent();
        return component != null && component.interact(opcode, actionIndex);
    }

    public boolean unequip() {
        return interact(ActionOpcodes.INTERFACE_ACTION);
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public boolean containsAction(Predicate<String> predicate) {
        for (String action : getActions()) {
            if (action != null && predicate.test(action)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAction(String action) {
        return containsAction(p -> p.equalsIgnoreCase(action));
    }
}
