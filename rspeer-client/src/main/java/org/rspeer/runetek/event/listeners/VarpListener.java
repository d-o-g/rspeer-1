package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.VarpEvent;

/**
 * Created by Spencer on 31/01/2018.
 */
public interface VarpListener extends EventListener {
    void notify(VarpEvent e);
}
