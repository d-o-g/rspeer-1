package org.rspeer.runetek.event.listeners;


import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.LoginMessageEvent;

/**
 * @Deprecated See {@link LoginResponseListener}
 */
@Deprecated
public interface LoginMessageListener extends EventListener {
    void notify(LoginMessageEvent event);
}
