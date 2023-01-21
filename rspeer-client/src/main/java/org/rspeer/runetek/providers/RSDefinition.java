package org.rspeer.runetek.providers;

import org.rspeer.runetek.providers.annotations.Synthetic;

import java.util.function.Predicate;

/*
 * TODO should this apply to all definitions? not just item/npc/obj? so defs like graphicdef, hitsplatdef
 */
@Synthetic
public interface RSDefinition extends RSDoublyNode {

    int getId();

    String getName();

    String[] getActions();

    default boolean containsAction(Predicate<String> action) {
        String[] actions = getActions();
        if (actions != null && action != null) {
            for (String action0 : actions) {
                if (action0 != null && action.test(action0)) {
                    return true;
                }
            }
        }
        return false;
    }
}
