package org.rspeer.startup;

import com.beust.jcommander.JCommander;
import org.rspeer.BotArgs;
import org.rspeer.QuickStartArgs;
import org.rspeer.RSPeer;
import org.rspeer.api_services.PingService;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.*;
import org.rspeer.instancing.ClientInfoAggregator;
import org.rspeer.startup.children.SessionValidator;
import org.rspeer.ui.Log;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class StartupManager {

    public void execute(String[] args, Runnable onFinish) {
        try {
            addStartingSplash();
            RsPeerApi.setOnRetryCallback(s -> {
                Log.severe("Failed to make request to RSPeer Servers. Attempt: " + s.getAttemptCount() + ". Trying again.");
            });

            addShutDownHook();
            BotPreferences.load();
            RsPeerExecutor.execute(() -> RSPeer.setOriginalIpOnce(HttpUtil.getIpAddress()));
            RsPeerExecutor.execute(RsPeerApi::initialize);
            PingService.getInstance().start(ClientInfoAggregator::execute);
            Util.deleteRandomDat();

            Locale.setDefault(Locale.UK);

            SessionValidator sessionValidator = new SessionValidator();
            sessionValidator.execute(()
                    -> onAfterValidation(args, onFinish));
        } catch (Throwable e) {
            EventQueue.invokeLater(() -> {
                JOptionPane.showMessageDialog(RSPeer.getView(), e.getMessage(), "Failed to start.", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void addStartingSplash() {
        EventQueue.invokeLater(() -> {
            RSPeer.getView().getSplash().setState("Starting RSPeer...");
            RSPeer.getView().getSplash().setProgress(10);
            RSPeer.getView().revalidate();
        });
    }

    private void onAfterValidation(String[] args, Runnable callback) {
        validateAndSetBotArgs(args);
        if(RSPeer.getView().getSplash() == null) {
            addStartingSplash();
        }
        callback.run();
    }

    private void validateAndSetBotArgs(String[] args) {
        BotArgs botArgs = new BotArgs();
        JCommander.newBuilder().addObject(botArgs).acceptUnknownOptions(true).build().parse(args);
        RSPeer.setBotArgs(botArgs);
        if (botArgs.getQuickStart() == null) {
            return;
        }
        QuickStartArgs qsArgs;
        try {
            qsArgs = QuickStartArgs.parse(botArgs.getQuickStart());
        } catch (Exception e) {
            Log.severe(e);
            onStartupException(e);
            return;
        }
        RSPeer.setQuickStartArgs(qsArgs);
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Failsafe.shutDown();
                PingService.getInstance().onClientClose();
            } catch (Throwable e) {
                System.out.println(e.toString());
            }
        }));
    }

    public void onStartupException(Exception e) {
        e.printStackTrace();
        EventQueue.invokeLater(() -> {
            try {
                RSPeer.getView().remove(RSPeer.getView().getSplash());
                RSPeer.getView().revalidate();
                RSPeer.getView().getStartupFailure().getError().setText(
                        e.getMessage() != null ? e.getMessage() : e.toString());
                RSPeer.getView().revalidate();
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
    }

}
