package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.Identifiable;

public interface RSBefriendedPlayer extends RSAssociate, Identifiable {

    default int getId() {
        throw new UnsupportedOperationException();
    }

    default String getName() {
        return Functions.mapOrNull(this::getDisplayName, RSNamePair::getClean);
    }

    default String getLastName() {
        return Functions.mapOrNull(this::getPreviousName, RSNamePair::getClean);
    }
}
