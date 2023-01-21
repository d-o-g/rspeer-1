package org.rspeer.runetek.api.movement.global.augmenting;

import org.rspeer.runetek.api.movement.global.Mapo;
import org.rspeer.runetek.api.movement.global.PathNode;
import org.rspeer.runetek.api.movement.position.Position;

import java.util.function.BooleanSupplier;

public abstract class TeleportNode extends PathNode {

    private BooleanSupplier usable; //additional varps, varpbits, diaries etc
    private boolean wilderness;

    public TeleportNode(Type type, Position position) {
        super(type, position);
    }

    public TeleportNode condition(BooleanSupplier usable) {
        this.usable = usable;
        return this;
    }

    public TeleportNode wilderness() {
        wilderness = true;
        return this;
    }

    @Override
    public boolean validate() {
        if (!Mapo.TraversalOption.TELEPORTATION.isActive()) {
            return false;
        }

        if (!Mapo.TraversalOption.WILDERNESS.isActive() && wilderness) {
            return false;
        }

        if (usable != null && !usable.getAsBoolean()) {
            return false;
        }

        return true;
    }
}
