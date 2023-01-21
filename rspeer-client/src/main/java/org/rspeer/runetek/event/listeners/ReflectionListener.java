package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ReflectionEvent;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
public interface ReflectionListener extends EventListener {
    void notify(ReflectionEvent event);
}
