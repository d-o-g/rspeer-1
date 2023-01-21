package org.rspeer.runetek.api.movement.pathfinding.hpa.cache;

import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.position.Position;

/**
 * Created by Zachary Herridge on 7/11/2018.
 */
@Deprecated
public abstract class AbstractHpaCache {

    private static AbstractHpaCache instance;

    public static AbstractHpaCache getInstance() {
        return instance;
    }

    public static void setInstance(AbstractHpaCache instance) {
        AbstractHpaCache.instance = instance;
    }

    public abstract HpaPath getPath(Position source, Position destination);

    public abstract void decache(HpaPath hpaPath);

    public abstract void refresh(HpaPath hpaPath);
}
