package org.rspeer.script;

import org.rspeer.RSPeer;
import org.rspeer.api_services.PingService;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.script.provider.RemoteScriptSource;
import org.rspeer.script.provider.ScriptSource;
import org.rspeer.ui.BotView;
import org.rspeer.ui.Log;
import org.rspeer.ui.commons.SwingResources;
import org.rspeer.ui.component.BotTitlePaneHelper;

public final class ScriptExecutor {

    private static Script current;
    private static ScriptSource scriptSource;

    public static Script getCurrent() {
        return current;
    }

    public static ScriptSource getScriptSource() {
        return scriptSource;
    }

    public static boolean isRemoteScript() {
        return scriptSource instanceof RemoteScriptSource;
    }

    public static boolean isRunning() {
        return current != null && !current.isStopping();
    }

    public static void stop() {
        if (current != null) {
            try {
                current.onStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            current.setStopping(true);
            current.interrupt();
            current = null;
            PingService.getInstance().ping();
            BotView view = RSPeer.getView();
            view.getToolBar().getRun().setText(SwingResources.PLAY);
            view.getToolBar().getRun().setToolTipText("Play");
            view.getToolBar().getStop().setEnabled(false);
            view.getToolBar().getRefresh().setVisible(false);
            RsPeerExecutor.execute(BotTitlePaneHelper::refreshFrameTitle);
            GameCanvas.setInputEnabled(true);
        }
    }

    public static boolean isPaused() {
        return current != null && current.isPaused();
    }

    public static void pause() {
        if (current != null) {
            BotView view = RSPeer.getView();
            view.getToolBar().getRun().setText(SwingResources.PLAY);
            view.getToolBar().getRun().setToolTipText("Play");
            current.setPaused(true);
            GameCanvas.setInputEnabled(true);
        }
    }

    public static void resume() {
        if (current != null) {
            BotView view = RSPeer.getView();
            view.getToolBar().getRun().setText(SwingResources.PAUSE);
            view.getToolBar().getRun().setToolTipText("Pause");
            current.setPaused(false);
            GameCanvas.setInputEnabled(false);
        }
    }

    @Deprecated
    public static void enableReloadScriptOnChange() {
        Log.info("Enable reload on script change has been removed.");
    }

    public static void start(ScriptSource source, Script script) {
        if (current != null) {
            throw new IllegalStateException("A Script is already running");
        } else if (script != null) {
            ScriptExecutor.scriptSource = source;
            BotView view = RSPeer.getView();
            try {
                current = script;
                PingService.getInstance().ping();
                view.getToolBar().getRun().setText(SwingResources.PAUSE);
                view.getToolBar().getRun().setToolTipText("Pause");
                view.getToolBar().getStop().setEnabled(true);
                view.getToolBar().getRefresh().setVisible(true);
                GameCanvas.setInputEnabled(false);
                RsPeerExecutor.execute(BotTitlePaneHelper::refreshFrameTitle);
                current.start();
            } catch (Exception e) {
                Log.severe(e);
                e.printStackTrace();
                view.getToolBar().getRun().setText(SwingResources.PLAY);
                view.getToolBar().getRun().setToolTipText("Play");
                view.getToolBar().getStop().setEnabled(false);
                view.getToolBar().getRefresh().setVisible(false);
                ScriptExecutor.stop();
                PingService.getInstance().ping();
                GameCanvas.setInputEnabled(true);
            }
        }
    }
}
