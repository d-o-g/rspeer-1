package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.AnimationEvent;
import org.rspeer.runetek.event.types.TargetEvent;

/**
 * Created by Spencer on 18/07/2018.
 */
public interface AnimationListener extends EventListener {
    void notify(AnimationEvent e);
}
