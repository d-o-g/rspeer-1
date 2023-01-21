package org.rspeer.runetek.api.component;

import org.rspeer.runetek.api.Game;

/**
 * Created by Spencer on 22/03/2018.
 */
public enum AttackOptionPriority {

    DEPENDS_ON_COMBAT_LEVELS,
    ALWAYS_RIGHT_CLICK,
    LEFT_CLICK_WHERE_AVAILABLE,
    HIDDEN;

    public static AttackOptionPriority getPlayerSetting() {
        int id = Game.getClient().getPlayerActionPriority().getId();
        for (AttackOptionPriority priority : AttackOptionPriority.values()) {
            if (priority.ordinal() == id) {
                return priority;
            }
        }
        throw new IllegalStateException("Unhandled player action priority: " + id + " (Please report!)");
    }

    public static AttackOptionPriority getNpcSetting() {
        int id = Game.getClient().getNpcActionPriority().getId();
        for (AttackOptionPriority priority : AttackOptionPriority.values()) {
            if (priority.ordinal() == id) {
                return priority;
            }
        }
        throw new IllegalStateException("Unhandled npc action priority: " + id + " (Please report!)");
    }
}
