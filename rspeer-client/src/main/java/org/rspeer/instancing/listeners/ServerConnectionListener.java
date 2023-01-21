package org.rspeer.instancing.listeners;

import org.rspeer.RSPeer;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.event.listeners.ServerConnectionChangeListener;
import org.rspeer.runetek.event.types.ServerConnectionEvent;
import org.rspeer.ui.Log;
import org.rspeer.ui.component.BotTitlePaneHelper;

public class ServerConnectionListener implements ServerConnectionChangeListener {

    private ServerConnectionEvent.ServerConnectionStatus last;

    public ServerConnectionListener() {
        Game.getEventDispatcher().register(this);
    }

    @Override
    public void notify(ServerConnectionEvent e) {
        switch (e.getStatus()) {
            case CONNECTED:
                onConnect();
                last = e.getStatus();
                break;
            case DISCONNECTED:
                onDisconnect();
                last = e.getStatus();
                break;
        }
    }

    private void onConnect() {
        if(last != null && last == ServerConnectionEvent.ServerConnectionStatus.DISCONNECTED) {
            Log.fine("Successfully reconnected to RSPeer servers.");
        }
        BotTitlePaneHelper.refreshFrameTitle();
    }

    private void onDisconnect() {
        Log.severe("Your client has lost connection to the RSPeer server for over 5 minutes. This can happen if you are over your instance limit. " +
                "Your client will be closed in 5 minutes unless connection is regained.");
        RSPeer.getView().setTitle("RSPeer | Closing In 5 Minutes");
    }
}
