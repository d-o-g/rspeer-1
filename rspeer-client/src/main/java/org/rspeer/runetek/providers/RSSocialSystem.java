package org.rspeer.runetek.providers;

/**
 * Created by Spencer on 01/02/2018.
 */
public interface RSSocialSystem extends RSProvider {

    RSFriendListContext getFriendListContext();

    RSIgnoreListContext getIgnoreListContext();

}
