package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.RenderEvent;

public interface RenderListener extends EventListener {
    void notify(RenderEvent event);
}
