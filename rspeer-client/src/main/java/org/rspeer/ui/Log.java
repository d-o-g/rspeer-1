package org.rspeer.ui;

import org.rspeer.RSPeer;
import org.rspeer.ui.component.log.LogPane;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Spencer on 26/01/2018.
 */
public final class Log {

    private Log() {
        throw new IllegalAccessError();
    }

    public static void log(Level level, String topic, Object msg) {
        LogRecord record = new LogRecord(level, msg.toString());
        record.setLoggerName(topic);
        if (RSPeer.getView() == null) {
            return;
        }
        LogPane pane = RSPeer.getView().getLogPane();
        if (pane != null) {
            pane.log(record);
        }
    }

    public static void info(String topic, Object msg) {
        log(Level.INFO, topic, msg);
    }

    public static void info(Object msg) {
        info("Info", msg);
    }

    public static void info(String msg) {
        info("Info", msg);
    }

    public static void fine(String topic, Object msg) {
        log(Level.FINE, topic, msg);
    }

    public static void fine(Object msg) {
        fine("Fine", msg);
    }

    public static void severe(String topic, Object msg) {
        log(Level.SEVERE, topic, msg);
    }

    public static void severe(Object msg) {
        severe("SEVERE", msg);
    }

    public static void severe(Throwable e) {
        RSPeer.getView().getLogPane().log(e);
    }

    public static void setMinified(boolean minified) {
        RSPeer.getView().getLogPane().setMinified(minified);
    }
}
