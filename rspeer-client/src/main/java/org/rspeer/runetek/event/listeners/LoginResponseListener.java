package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.LoginResponseEvent;

/**
 * Created by Spencer on 24/08/2018.
 */
public interface LoginResponseListener extends EventListener {
    void notify(LoginResponseEvent e);
}
