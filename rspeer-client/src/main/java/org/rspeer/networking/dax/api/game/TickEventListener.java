package org.rspeer.networking.dax.api.game;

import org.rspeer.networking.dax.api.utils.DaxListener;

public interface TickEventListener extends DaxListener<TickEvent> {
    @Override
    void trigger(TickEvent event);
}
