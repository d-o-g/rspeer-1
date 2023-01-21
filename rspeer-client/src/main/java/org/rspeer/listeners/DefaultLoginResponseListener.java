package org.rspeer.listeners;

import org.rspeer.api_services.PingService;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.event.listeners.LoginResponseListener;
import org.rspeer.runetek.event.types.LoginResponseEvent;

public class DefaultLoginResponseListener implements LoginResponseListener {

    public DefaultLoginResponseListener() {
        Game.getEventDispatcher().register(this);
    }

    @Override
    public void notify(LoginResponseEvent e) {
        if(e.getResponse() == LoginResponseEvent.Response.ACCOUNT_DISABLED) {
            PingService.getInstance().onBan();
        }
    }
}
