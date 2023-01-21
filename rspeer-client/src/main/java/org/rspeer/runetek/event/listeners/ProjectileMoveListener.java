package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ProjectileMoveEvent;

public interface ProjectileMoveListener extends EventListener {
    void notify(ProjectileMoveEvent event);
}
