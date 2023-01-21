package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.NpcSpawnListener;

public final class NpcSpawnEvent extends Event<Npc> {

    public NpcSpawnEvent(Npc source) {
        super(source);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof NpcSpawnListener) {
            ((NpcSpawnListener) listener).notify(this);
        }
    }
}
