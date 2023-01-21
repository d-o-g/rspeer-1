package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.providers.annotations.Synthetic;

public interface RSPickable extends RSEntity {
    int getId();

    int getStackSize();

    int getSceneX();

    int getSceneY();

    int getFloorLevel();

    @Synthetic
    Pickable getWrapper();
}