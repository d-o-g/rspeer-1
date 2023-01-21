package org.rspeer.runetek.event.types;

import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ItemTableListener;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.rspeer.runetek.providers.RSItemTable;

/**
 * Created by Spencer on 31/01/2018.
 */
public final class ItemTableEvent extends Event<RSItemTable> {

    private final int tableKey;
    private final int index, id, stackSize;
    private final RSItemDefinition definition;

    public ItemTableEvent(RSItemTable table, int tableKey, int index, int id, int stackSize) {
        super(table);
        this.tableKey = tableKey;
        this.index = index;
        this.id = id;
        this.stackSize = stackSize;
        definition = Definitions.getItem(id);
    }

    public int getIndex() {
        return index;
    }

    public int getOldId() {
        int[] ids = getSource().getIds();
        return ids != null && index < ids.length ? ids[index] : -1;
    }

    public int getId() {
        return id;
    }

    public int getOldStackSize() {
        int[] stacks = getSource().getStackSizes();
        return stacks != null && index < stacks.length ? stacks[index] : -1;
    }

    public RSItemDefinition getOldDefinition() {
        return Definitions.getItem(getOldId());
    }

    public RSItemDefinition getDefinition() {
        return definition;
    }

    public int getStackSize() {
        return stackSize;
    }

    public ChangeType getChangeType() {
        if (id != -1 && id == getOldId()) {
            return stackSize < getOldStackSize() ? ChangeType.ITEM_REMOVED : ChangeType.ITEM_ADDED;
        }
        return id == -1 ? ChangeType.ITEM_REMOVED : ChangeType.ITEM_ADDED;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ItemTableListener) {
            ((ItemTableListener) listener).notify(this);
        }
    }

    public int getTableKey() {
        return tableKey;
    }

    public enum ChangeType {
        ITEM_REMOVED,
        ITEM_ADDED;
    }
}
