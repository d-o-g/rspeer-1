package org.rspeer.runetek.event.types;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.SkillListener;

/**
 * Created by Spencer on 17/07/2018.
 */
public final class SkillEvent extends Event<Skill> {

    public static final int TYPE_EXPERIENCE = 0;
    public static final int TYPE_LEVEL = 1;
    public static final int TYPE_TEMPORARY_LEVEL = 2;

    private final int type;
    private final int previous;
    private final int current;

    public SkillEvent(Skill source, int type, int previous, int current) {
        super(source);
        this.type = type;
        this.previous = previous;
        this.current = current;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof SkillListener) {
            ((SkillListener) listener).notify(this);
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
