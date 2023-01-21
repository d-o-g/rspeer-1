package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ProjectileSpawnEvent;

public interface ProjectileSpawnListener extends EventListener {
    void notify(ProjectileSpawnEvent e);
}
