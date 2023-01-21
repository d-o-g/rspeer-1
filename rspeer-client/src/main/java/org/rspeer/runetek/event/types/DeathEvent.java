package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.DeathListener;

/**
 * Created by Spencer on 31/03/2018.
 */
public final class DeathEvent extends Event<PathingEntity> {

    public DeathEvent(PathingEntity source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof DeathListener) {
            ((DeathListener) listener).notify(this);
        }
    }
}
