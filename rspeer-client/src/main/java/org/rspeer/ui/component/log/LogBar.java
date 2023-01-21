package org.rspeer.ui.component.log;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Spencer on 26/01/2018.
 */
public final class LogBar extends JLabel {

    public LogBar() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public void setText(String text) {
        LogRecord record = new LogRecord(Level.INFO, text);
        setText(record);
    }

    public void setText(LogRecord record) {
        setForeground(Color.GRAY.brighter());

        if (record.getLevel() == Level.SEVERE) {
            setForeground(Color.RED.brighter());
        }

        if (record.getLevel() == Level.WARNING) {
            setForeground(Color.ORANGE);
        }

        if ((record.getLevel() == Level.FINE)
                || (record.getLevel() == Level.FINER)
                || (record.getLevel() == Level.FINEST)) {
            setForeground(Color.GREEN);
        }

        Object[] params = record.getParameters();
        if (params != null) {
            for (Object param : params) {
                if (param != null) {
                    if (param instanceof Color) {
                        setForeground((Color) param);
                    } else if (param instanceof Font) {
                        setFont((Font) param);
                    }
                }
            }
        }
        super.setText(record.getMessage());
    }
}
