package org.rspeer.networking.dax.walker.engine.definitions;

import com.allatori.annotations.DoNotRename;

import java.util.function.BooleanSupplier;

/**
 * At any time this method returns true, Walker will exit out
 * and return false since it has not successfully traversed
 * the path.
 */
@DoNotRename
public interface WalkCondition extends BooleanSupplier {

    @DoNotRename
    default WalkCondition and(WalkCondition walkCondition) {
        return () -> WalkCondition.this.getAsBoolean() && walkCondition.getAsBoolean();
    }

    @DoNotRename
    default WalkCondition or(WalkCondition walkCondition) {
        return () -> WalkCondition.this.getAsBoolean() || walkCondition.getAsBoolean();
    }

}
