package org.rspeer.script;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;

public abstract class LoopTask extends Thread {

    private boolean paused = false;
    private boolean stopping = false;
    private long count;

    public abstract int loop();

    protected void processBlockingEvents() {

    }

    protected boolean pendingBlockingEvents() {
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (paused && !stopping) {
                    Time.sleep(100);
                    continue;
                }
                if (!stopping && pendingBlockingEvents()) {
                    processBlockingEvents();
                    Time.sleep(100);
                } else if (!stopping) {
                    int loop = loop();
                    count++;
                    if (loop >= 0) {
                        Time.sleep(loop);
                    } else {
                        ScriptExecutor.stop();
                        break;
                    }
                } else {
                    ScriptExecutor.stop();
                    break;
                }
            } catch (Exception e) {
                Log.severe(e);
                Time.sleep(300);
                e.printStackTrace();
            }
        }
    }

    public final boolean isPaused() {
        return paused;
    }

    public final void setPaused(boolean paused) {
        this.paused = paused;

        if (paused) {
            onPause();
        } else {
            onResume();
        }
    }

    public final boolean isStopping() {
        return stopping;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onStop() {

    }

    public void onUncaughtException(Exception e) {

    }

    public long getLoopCount() {
        return count;
    }
}