package org.rspeer.runetek.adapter;

import org.rspeer.runetek.api.input.menu.interaction.InteractDriver;

import java.util.Arrays;
import java.util.function.Predicate;

public interface Interactable {

    /**
     * Interacts using the specified opcode
     * Note: This method is unsupported for InterfaceComponents
     * @see org.rspeer.runetek.api.input.menu.ActionOpcodes
     * @param opcode The opcode to interact with
     * @return {@code true} if the interaction was successful
     */
    default boolean interact(int opcode) {
        return InteractDriver.INSTANCE.interact(this, opcode);
    }

    /**
     * @param action The action to interact with
     * @return {@code true} if the interaction was successful
     */
    default boolean interact(String action){
        return InteractDriver.INSTANCE.interact(this, action);
    }

    default boolean interact(Predicate<String> predicate) {
        for (String action : getActions()) {
            if (action != null && predicate.test(action)) {
                return interact(action);
            }
        }
        return predicate.test("") && interact("");
    }

    /**
     * @return An array of actions, filtering nulls
     */
    String[] getActions();

    /**
     * @return An array of actions, no filtering
     */
    String[] getRawActions();

    /**
     * @param predicate The predicate used to test the actions
     * @return {@code true} if any of the actions satisfy the predicate
     */
    default boolean containsAction(Predicate<String> predicate) {
        for (String action : getActions()) {
            if (action != null && predicate.test(action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param action The action to check for
     * @return {@code true} if this interactable contains the specified action
     */
    default boolean containsAction(String action) {
        return containsAction(p -> p.equalsIgnoreCase(action));
    }

    default boolean click() {
        return interact(x -> true);
    }
}
