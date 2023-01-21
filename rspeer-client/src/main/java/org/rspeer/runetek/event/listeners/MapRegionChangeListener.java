package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.MapRegionChangedEvent;

/**
 * Created by Zachary Herridge on 2/13/2018.
 */
public interface MapRegionChangeListener extends EventListener{
    void notify(MapRegionChangedEvent e);
}
