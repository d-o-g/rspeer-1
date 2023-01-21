package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.MapRegionChangeListener;

/**
 * Created by Zachary Herridge on 2/13/2018.
 */
public final class MapRegionChangedEvent extends Event<Integer> {

    public MapRegionChangedEvent(Integer sourcey) {
        super(sourcey);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof MapRegionChangeListener) {
            ((MapRegionChangeListener) listener).notify(this);
        }
    }
}
