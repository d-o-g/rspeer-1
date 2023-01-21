package org.rspeer.runetek.api.local;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;

public final class Health {

    private Health() {
        throw new IllegalAccessError();
    }

    /**
     * @return The current health of the local player
     */
    public static int getCurrent() {
        return Skills.getCurrentLevel(Skill.HITPOINTS);
    }

    /**
     * @return The maximum base health (or level) of the local player)
     */
    public static int getLevel() {
        return Skills.getLevel(Skill.HITPOINTS);
    }

    /**
     * @return The current health of the local player as a percentage
     */
    public static int getPercent() {
        try {
            return getCurrent() * 100 / getLevel();
        } catch (Exception e) {
            return -1;
        }
    }
}
