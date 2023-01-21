package org.rspeer.runetek.api.network;

import java.util.Set;

public class RSPeerUser {

    public String username;
    public Set<String> groups;

    public RSPeerUser(String username, Set<String> groups) {
        this.username = username;
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getGroups() {
        return groups;
    }
}
