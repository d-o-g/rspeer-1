package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.BankLoadEvent;

/**
 * Created by jasper on 04/08/18.
 */
public interface BankLoadListener extends EventListener {
    void notify(BankLoadEvent event);
}
