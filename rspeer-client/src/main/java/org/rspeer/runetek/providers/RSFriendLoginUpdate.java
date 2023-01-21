package org.rspeer.runetek.providers;

public interface RSFriendLoginUpdate extends RSProvider {

    RSNamePair getNamePair();

    int getTime();

    short getWorld();
}
