package org.rspeer.runetek.api.commons.predicate;

import org.rspeer.runetek.adapter.scene.PathingEntity;

import java.util.function.Predicate;

public class OverheadTextPredicate<I extends PathingEntity> implements Predicate<I> {

    private final String[] dialogues;
    private final boolean contains;

    public OverheadTextPredicate(boolean contains, String... dialogues) {
        this.contains = contains;
        this.dialogues = dialogues;
    }

    public OverheadTextPredicate(String... dialogues) {
        this(false, dialogues);
    }

    @Override
    public boolean test(I i) {
        if (i.getName() == null) {
            return false;
        }
        for (String name : dialogues) {
            if (contains ? i.getOverheadText().toLowerCase().contains(name.toLowerCase()) : i.getOverheadText().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
