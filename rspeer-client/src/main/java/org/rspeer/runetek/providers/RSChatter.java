package org.rspeer.runetek.providers;

/**
 * Created by Spencer on 01/02/2018.
 */
public interface RSChatter extends RSProvider {

    RSNamePair getDisplayName();

    RSNamePair getPreviousName();
}
