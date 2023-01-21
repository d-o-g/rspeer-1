package org.rspeer.startup;

import org.rspeer.QuickStartService;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RemoteMessageService;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.instancing.listeners.ServerConnectionListener;
import org.rspeer.listeners.DefaultRemoteMessageListener;
import org.rspeer.networking.acuity.AcuityServices;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;

public class GameLoadedManager {

    public void execute() {
        RsPeerExecutor.execute(() -> {
            AcuityServices.start();
            double ours = RsPeerApi.getOurVersion();
            double live = RsPeerApi.getBotVersion();
            String outOfDate = "Your RSPeer version is out of date from live (v" + live + ").";
            String message = "Welcome " + RsPeerApi.getCurrentUserName();
            if(ours < live) {
                message += ". " + outOfDate;
            }
            Log.fine(message);
        });
        executeQuickLaunch();
        RemoteMessageService.poll();
        setupInternalListeners();
    }

    private void executeQuickLaunch() {
        if (RSPeer.getQuickStartArgs() == null && RSPeer.getBotArgs() == null) {
           return;
        }
        Time.sleep(1000);
        QuickStartService qss = new QuickStartService(RSPeer.getQuickStartArgs(), RSPeer.getBotArgs());
        qss.apply();
    }

    private void setupInternalListeners() {
        new DefaultRemoteMessageListener();
        new ServerConnectionListener();
        new DefaultRemoteMessageListener();
    }
}
