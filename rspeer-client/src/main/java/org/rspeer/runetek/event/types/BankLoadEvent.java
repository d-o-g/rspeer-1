package org.rspeer.runetek.event.types;

import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.BankLoadListener;
import org.rspeer.runetek.providers.RSItemTable;
import org.rspeer.runetek.providers.RSScriptEvent;

/**
 * Created by jasper on 04/08/18.
 */
public final class BankLoadEvent extends ScriptEvent {

    public BankLoadEvent(RSScriptEvent source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof BankLoadListener) {
            ((BankLoadListener) listener).notify(this);
        }
    }

    public RSItemTable getBankItemTable() {
        return ItemTables.lookup(ItemTables.BANK);
    }
}
