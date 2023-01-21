package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ObjectSpawnEvent;

/**
 * Created by Spencer on 30/01/2018.
 */
public interface ObjectSpawnListener extends EventListener {
    void notify(ObjectSpawnEvent e);
}
