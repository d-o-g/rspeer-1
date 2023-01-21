package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.commons.predicate.Predicates;

import java.util.function.Predicate;

public interface RSChatstream extends RSProvider {

    RSChatline[] getBuffer();

    int getCaret();

    default RSChatline getLine(int index) {
        return index >= 0 && index < getCaret() ? getBuffer()[index] : null;
    }

    default RSChatline getLine(Predicate<? super RSChatline> predicate) {
        return Predicates.firstMatching(predicate, getBuffer());
    }
}
