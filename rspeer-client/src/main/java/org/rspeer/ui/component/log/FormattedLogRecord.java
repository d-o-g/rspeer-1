package org.rspeer.ui.component.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by Spencer on 26/01/2018.
 */
public final class FormattedLogRecord {

    private static final Formatter dateFormatter = new Formatter() {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

        @Override
        public String format(LogRecord record) {
            if (record.getLoggerName() == null) {
                record.setLoggerName("Log");
            }
            String[] receiverPath = record.getLoggerName().split("\\.");
            String receiver = receiverPath[receiverPath.length - 1];
            int size = 10;
            String append = "...";
            return String.format(
                    "[%s] %-" + size + "s %s %s",
                    dateFormat.format(record.getMillis()),
                    receiver.length() > size ? receiver.substring(0,
                            size - append.length())
                            + append : receiver, record.getMessage(),
                    throwableToString(record.getThrown()));
        }
    };
    private static final Formatter copyFormatter = new Formatter() {

        @Override
        public String format(LogRecord record) {
            StringBuilder result = new StringBuilder().append("[").append(record.getLevel().getName()).append("] ").
                    append(new Date(record.getMillis())).append(": ").append(record.getLoggerName()).append(": ");
            if (record.getMessage() != null) {
                result.append(record.getMessage());
            }

            if (record.getThrown() != null) {
                result.append(throwableToString(record.getThrown()));
            }
            result.append(System.getProperty("line.separator"));
            return result.toString();
        }
    };

    private final LogRecord record;
    private final String formatted;

    public FormattedLogRecord(LogRecord record) {
        this.record = record;
        formatted = dateFormatter.format(record);
    }

    private static String throwableToString(Throwable t) {
        if (t != null) {
            Writer exception = new StringWriter();
            PrintWriter printWriter = new PrintWriter(exception);
            t.printStackTrace(printWriter);
            return exception.toString();
        }
        return "";
    }

    @Override
    public String toString() {
        return copyFormatter.format(record);
    }

    public LogRecord getBase() {
        return record;
    }

    public String getFormatted() {
        return formatted;
    }
}