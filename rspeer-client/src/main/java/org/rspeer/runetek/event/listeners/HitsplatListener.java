package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.HitsplatEvent;

/**
 * Created by Spencer on 31/03/2018.
 */
public interface HitsplatListener extends EventListener {
    void notify(HitsplatEvent e);
}
