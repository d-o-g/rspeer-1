package org.rspeer.listeners;

import org.rspeer.RSPeer;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.event.listeners.RemoteMessageListener;
import org.rspeer.runetek.event.types.RemoteMessageEvent;
import org.rspeer.script.ScriptReloader;
import org.rspeer.ui.Log;

public class DefaultRemoteMessageListener implements RemoteMessageListener {

    public DefaultRemoteMessageListener() {
        Game.getEventDispatcher().register(this);
    }

    @Override
    public void notify(RemoteMessageEvent e) {
        String message = e.getSource().getMessage();
        if(message.equals(":kill")) {
            Log.severe("Closing client, reason: " + e.getSource().getSource());
            RSPeer.shutdown();
            return;
        }
        if(message.equals(":reload_script")) {
            ScriptReloader reloader = new ScriptReloader();
            reloader.execute();
        }
        if(e.getSource().getSource().equals("log:critical")) {
            Log.severe(message);
        }
    }
}
