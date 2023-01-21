package org.rspeer.runetek.providers;

/**
 * Created by Spencer on 01/02/2018.
 */
public interface RSClanSystem extends RSChatterContext {

    String getChannelName();

    String getChannelOwner();

    byte getLocalPlayerRank();
}
