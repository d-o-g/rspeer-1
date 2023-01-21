package org.rspeer.script.provider;

import org.rspeer.script.ScriptCategory;

/**
 * Created by Spencer on 04/02/2018.
 */
public final class RemoteScriptSource extends ScriptSource {

    private final int id;
    private final int totalUsers;
    private final String forumThread;

    public RemoteScriptSource(String name, double version, String description, String developer, ScriptCategory category, int id, int totalUsers, String forumThread) {
        super(name, version, description, developer, category);
        this.id = id;
        this.totalUsers = totalUsers;
        this.forumThread = forumThread;
    }

    public boolean isLocal() {
        return false;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public String getForumThread() {
        return forumThread;
    }

    public int getId() {
        return id;
    }
}
