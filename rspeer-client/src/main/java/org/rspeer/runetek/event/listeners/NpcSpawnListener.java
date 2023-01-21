package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.NpcSpawnEvent;

public interface NpcSpawnListener extends EventListener {
    void notify(NpcSpawnEvent e);
}
