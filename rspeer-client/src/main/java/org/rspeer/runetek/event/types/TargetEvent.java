package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.TargetListener;

/**
 * Created by Spencer on 18/07/2018.
 */
public final class TargetEvent extends Event<PathingEntity> {

    private final int oldIndex;
    private final int index;

    public TargetEvent(PathingEntity source, int oldIndex, int index) {
        super(source);
        this.oldIndex = oldIndex;
        this.index = index;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof TargetListener) {
            ((TargetListener) listener).notify(this);
        }
    }

    public int getOldIndex() {
        return oldIndex;
    }

    private PathingEntity resolve(int index) {
        if (index == -1) {
            return null;
        } else if (index < 32768) {
            return Npcs.getAt(index);
        }
        index -= 32768;
        return index == Game.getClient().getPlayerIndex() ? Players.getLocal() : Players.getAt(index);
    }

    public PathingEntity getOldTarget() {
        return resolve(oldIndex);
    }

    public PathingEntity getTarget() {
        return resolve(index);
    }

    public int getIndex() {
        return index;
    }
}
