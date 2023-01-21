package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.Projectile;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ProjectileSpawnListener;

public final class ProjectileSpawnEvent extends Event<Projectile> {

    public ProjectileSpawnEvent(Projectile source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ProjectileSpawnListener) {
            ((ProjectileSpawnListener) listener).notify(this);
        }
    }
}
