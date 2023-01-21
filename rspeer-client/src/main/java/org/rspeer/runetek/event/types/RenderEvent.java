package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.providers.RSGraphicsProvider;

import java.awt.*;

public final class RenderEvent extends Event<Graphics> {

    private final RSGraphicsProvider provider;

    public RenderEvent(Graphics source, RSGraphicsProvider provider) {
        super(source);
        this.provider = provider;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof RenderListener) {
            ((RenderListener) listener).notify(this);
        }
    }

    public RSGraphicsProvider getProvider() {
        return provider;
    }
}
