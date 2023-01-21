package org.rspeer;

import net.jodah.failsafe.function.CheckedRunnable;
import org.rspeer.api_services.Logger;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.injector.Injector;
import org.rspeer.injector.Modscript;
import org.rspeer.runetek.api.Game;
import org.rspeer.startup.GameLoadedManager;
import org.rspeer.startup.StartupManager;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Bootstrap {

    private StartupManager startup;
    private ScheduledFuture<?> gameLoaded;

    public Bootstrap() {
        startup = new StartupManager();
    }

    public static void main(String[] args) {
        Bootstrap two = new Bootstrap();
        two.start(args);
    }

    public void start(String[] args) {
        try {
            EventQueue.invokeAndWait(RSPeer::start);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().capture(e);
        }
        startup.execute(args, this::injectAndLoad);
    }

    private void injectAndLoad() {
        try {
            RSPeer.crawl();
            Injector injector = new Injector(Modscript.loadFromApi(), Configuration.GAMEPACK);
            injector.parseLibrary();
            injector.processLibrary();
            RSPeer.loadGame(injector.writeAndLoad());
            injector.close();
            CallbackPrintStream cps = new CallbackPrintStream(System.out, this::errorDuringLoad);
            System.setOut(cps);
            waitForLoadAndExecute();
        } catch (Exception e) {
            startup.onStartupException(e);
        }
    }

    private CheckedRunnable checkGameLoad() {
       return () -> {
           if (Game.getState() != Game.STATE_CREDENTIALS_SCREEN || RSPeer.getClient() == null) {
               return;
           }
           GameLoadedManager manager = new GameLoadedManager();
           manager.execute();
           gameLoaded.cancel(false);
       };
    }

    private void waitForLoadAndExecute() {
        gameLoaded = RsPeerExecutor.scheduleAtFixedRate(checkGameLoad(), 0, 100, TimeUnit.MILLISECONDS);
    }

    private void errorDuringLoad(String error) {
        if (error.equals("error_game_js5connect")) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(RSPeer.getView(), "The game could not connect to the OldSchool Runescape servers.\n"
                            + "Please check if your proxy is functional and your user/pass are correct.\n"
                            + "Only SOCKS5 proxies work with OSRS, trying to use HTTPS proxies will result in this error!\n",
                    "Connection error", JOptionPane.ERROR_MESSAGE));
        }
    }

    class CallbackPrintStream extends PrintStream {

        private final Consumer<String> callback;

        CallbackPrintStream(PrintStream org, Consumer<String> callback) {
            super(org);
            this.callback = callback;
        }

        @Override
        public void println(String line) {
            callback.accept(line);
            super.println(line);
        }

        public void println(int line) {
            this.println(String.valueOf(line));
        }
    }
}
