package org.rspeer.runetek.api.commons;

import org.rspeer.runetek.adapter.scene.PathingEntity;

public interface Targeter {

    PathingEntity getTarget();

    int getTargetIndex();
}
