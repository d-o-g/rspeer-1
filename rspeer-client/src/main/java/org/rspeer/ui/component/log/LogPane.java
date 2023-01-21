package org.rspeer.ui.component.log;

import org.rspeer.commons.BotPreferences;
import org.rspeer.ui.commons.SwingResources;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Spencer on 26/01/2018.
 */
public final class LogPane extends JScrollPane {

    private final LogTextArea area;
    private final LogBar bar;
    private LogFileBuffer fileBuffer;
    private boolean isMinified;

    public LogPane(LogTextArea area, LogBar bar) {

        this.area = area;
        this.bar = bar;
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setMinified(true);
        toggleFileLogging(BotPreferences.getInstance().isEnableFileLogging());
    }

    public void toggleFileLogging(boolean enabled) {
        if (enabled) {
            fileBuffer = new LogFileBuffer();
            return;
        }
        if (fileBuffer != null) {
            fileBuffer.dipose();
            fileBuffer = null;
        }
    }

    public boolean isMinified() {
        return isMinified;
    }

    public void setMinified(boolean minified) {
        this.isMinified = minified;
        if (minified) {
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            setViewportView(bar);
            SwingResources.setStrictSize(this, 771, 20);
        } else {
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            setViewportView(area);
            SwingResources.setStrictSize(this, 771, 120);
            area.scrollToBottom();
        }
    }

    public void log(LogRecord record) {
        FormattedLogRecord formatted = new FormattedLogRecord(record);
        if (fileBuffer != null) {
            fileBuffer.add(formatted.getFormatted());
        }
        area.log(formatted);
        bar.setText(record);
    }

    public void log(Throwable e) {
        LogRecord record = new LogRecord(Level.SEVERE, "SEVERE");
        record.setLoggerName("EXCEPTION");
        record.setSourceClassName("EXCEPTION");
        record.setSourceMethodName("");
        record.setThrown(e);
        log(record);
        e.printStackTrace();
    }
}
