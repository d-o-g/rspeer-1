package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.SkillEvent;

/**
 * Created by Spencer on 17/07/2018.
 */
public interface SkillListener extends EventListener {
    void notify(SkillEvent e);
}
