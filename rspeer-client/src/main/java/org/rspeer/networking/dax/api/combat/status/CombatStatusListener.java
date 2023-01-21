package org.rspeer.networking.dax.api.combat.status;

import org.rspeer.networking.dax.api.utils.DaxListener;

public interface CombatStatusListener extends DaxListener<AttackEvent> {
    @Override
    void trigger(AttackEvent event);
}
