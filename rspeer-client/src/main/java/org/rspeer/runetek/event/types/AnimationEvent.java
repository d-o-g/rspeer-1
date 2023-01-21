package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.AnimationListener;

/**
 * Created by Spencer on 18/07/2018.
 */
public final class AnimationEvent extends Event<PathingEntity> {

    /**
     * Indicates that an animation has started. Previous animation was -1, current animation is not -1
     **/
    public static final int TYPE_STARTED = 1;

    /**
     * Indicates that an animation has finished. Previous animation was not -1, current animation is -1
     **/
    public static final int TYPE_FINISHED = 2;

    /**
     * Indicates that an animation was updated. Current and previous are both different, and not -1
     **/
    public static final int TYPE_UPDATED = 3;

    /**
     * Indicates that an animation was extended. Current and previous are both identical, and not -1
     **/
    public static final int TYPE_EXTENDED = 4;

    private final int previous;
    private final int current;
    private final int type;

    public AnimationEvent(PathingEntity source, int previous, int current) {
        super(source);
        this.previous = previous;
        this.current = current;
        if (previous == -1 && current != -1) {
            type = TYPE_STARTED;
        } else if (previous != -1 && current == -1) {
            type = TYPE_FINISHED;
        } else if (previous != -1 && current != previous) {
            type = TYPE_UPDATED;
        } else { //previous == current
            type = TYPE_EXTENDED;
        }
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof AnimationListener) {
            ((AnimationListener) listener).notify(this);
        }
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }

    public int getType() {
        return type;
    }
}
