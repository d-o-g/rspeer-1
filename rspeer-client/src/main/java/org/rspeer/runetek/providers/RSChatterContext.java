package org.rspeer.runetek.providers;

import java.util.Map;

/**
 * Created by Spencer on 01/02/2018.
 */
public interface RSChatterContext extends RSProvider {

    int getCount();

    int getCapacity();

    RSChatter[] getChatters();

    Map getDisplayNameCache();

    Map getPreviousNameCache();
}
