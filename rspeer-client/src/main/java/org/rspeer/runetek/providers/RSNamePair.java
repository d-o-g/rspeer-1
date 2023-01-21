package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.StringCommons;

/**
 * Created by Spencer on 01/02/2018.
 */
public interface RSNamePair extends RSProvider {

    String getRaw();

    String getFormatted();

    default String getClean() {
        return Functions.mapOrDefault(this::getRaw, StringCommons::replaceJagspace, "");
    }
}
