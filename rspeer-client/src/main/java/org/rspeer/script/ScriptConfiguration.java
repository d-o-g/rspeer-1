package org.rspeer.script;

import com.allatori.annotations.DoNotRename;
import org.rspeer.runetek.api.movement.WebWalker;

@DoNotRename
public class ScriptConfiguration {

    @DoNotRename
    private WebWalker walker = WebWalker.ClientSettingsBased;

    /**
     * @return The WebWalker to use for the entire lifetime of this specific script run.
     */
    @DoNotRename
    public WebWalker getWalker() {
        return walker;
    }

    /**
     * @apiNote Sets the default WebWalker to use for the entire lifetime of this specific script run.
     * @apiNote Gets reset back to ClientSettingBased once the script ends.
     * @param walker
     */
    @DoNotRename
    public void setWalker(WebWalker walker) {
        this.walker = walker;
    }
}
