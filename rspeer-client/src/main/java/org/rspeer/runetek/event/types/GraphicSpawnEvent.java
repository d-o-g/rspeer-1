package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.GraphicSpawnListener;
import org.rspeer.runetek.providers.RSGraphicsObject;

public final class GraphicSpawnEvent extends Event<RSGraphicsObject> {

    public GraphicSpawnEvent(RSGraphicsObject source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof GraphicSpawnListener) {
            ((GraphicSpawnListener) listener).notify(this);
        }
    }
}
