package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.DeathEvent;

/**
 * Created by Spencer on 31/03/2018.
 */
public interface DeathListener extends EventListener {
    void notify(DeathEvent e);
}
