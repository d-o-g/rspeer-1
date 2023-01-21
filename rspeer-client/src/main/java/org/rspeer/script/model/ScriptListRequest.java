package org.rspeer.script.model;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class ScriptListRequest {

    @DoNotRename
    private ScriptQueryType type;

    @DoNotRename
    public ScriptQueryType getType() {
        return type;
    }

    @DoNotRename
    public void setType(ScriptQueryType type) {
        this.type = type;
    }
}
