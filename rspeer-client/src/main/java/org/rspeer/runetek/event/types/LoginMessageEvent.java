package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.LoginMessageListener;

/**
 * @Deprecated See {@link LoginResponseEvent}
 */
@Deprecated
public final class LoginMessageEvent extends Event {

    private final String responseLine1;
    private final String responseLine2;
    private final String responseLine3;

    public LoginMessageEvent(String responseLine1, String responseLine2, String responseLine3) {
        super("Static");
        this.responseLine1 = responseLine1;
        this.responseLine2 = responseLine2;
        this.responseLine3 = responseLine3;
    }

    public String getResponseLine1() {
        return responseLine1;
    }

    public String getResponseLine2() {
        return responseLine2;
    }

    public String getResponseLine3() {
        return responseLine3;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof LoginMessageListener) {
            ((LoginMessageListener) listener).notify(this);
        }
    }

    @Override
    public String toString() {
        return responseLine1 + responseLine2 + responseLine3;
    }
}
