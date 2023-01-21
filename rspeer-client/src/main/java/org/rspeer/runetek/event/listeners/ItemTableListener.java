package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ItemTableEvent;

/**
 * Created by Spencer on 31/01/2018.
 */
public interface ItemTableListener extends EventListener {
    void notify(ItemTableEvent e);
}
