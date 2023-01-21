package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.providers.RSScriptEvent;

/**
 * Created by Yasper on 04/08/18.
 */
public abstract class ScriptEvent extends Event<RSScriptEvent> {

    public ScriptEvent(RSScriptEvent source) {
        super(source);
    }

}
