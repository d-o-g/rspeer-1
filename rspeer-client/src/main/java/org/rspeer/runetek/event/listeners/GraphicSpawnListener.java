package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.GraphicSpawnEvent;

public interface GraphicSpawnListener extends EventListener {
    void notify(GraphicSpawnEvent e);
}
