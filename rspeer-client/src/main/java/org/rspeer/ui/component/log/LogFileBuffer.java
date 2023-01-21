package org.rspeer.ui.component.log;

import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.ui.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yasper on 06/08/18.
 * <p>
 * Acts as a buffer between the logger and a log file.
 * Writes to the log file either every MAX_BUFFER_SIZE messages or every 15 seconds.
 * </p>
 */
public class LogFileBuffer {

    public static final String LOG_DIRECTORY = Configuration.DATA + "logs" + File.separator;
    private static final int MAX_BUFFER_SIZE = 15;
    private static final int REFRESH_RATE = 15;
    private static final int MAX_LOG_FILES = 10;

    private final String fileName;
    private final LinkedList<String> buffer;

    private ScheduledFuture<?> future;

    public LogFileBuffer() {
        Log.fine("Starting file logger.");
        String instant = Instant.now().toString();
        String name = instant.split("\\.")[0] + ".txt";
        this.fileName = name.replaceAll(":", "");

        this.buffer = new LinkedList<>();

        future = RsPeerExecutor.schedule(this::clear, REFRESH_RATE, TimeUnit.SECONDS);
    }

    public boolean add(String string) {
        boolean result = buffer.add(string);
        if (result && buffer.size() >= MAX_BUFFER_SIZE) {
            clear();
        }

        return result;
    }

    public synchronized void clear() {
        if (buffer.size() == 0) {
            return;
        }

        File dir = new File(LOG_DIRECTORY);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.severe("We were unable to create a logs directory");
            return;
        }

        File[] files = dir.listFiles();
        if (files != null && files.length > MAX_LOG_FILES
                && !clearOldLogFiles(files)) {
            Log.info("Failed to delete old log files");
            return;
        }

        File logFile = new File(dir, fileName);
        try {
            if (!logFile.exists() && !logFile.createNewFile()) {
                Log.severe("We were unable to create the log file for the current session");
                return;
            }
        } catch (IOException e) {
            return;
        }

        try (FileWriter writer = new FileWriter(logFile, true)) {
            BufferedWriter bw = new BufferedWriter(writer);
            for (String element : buffer) {
                bw.append(element);
                bw.append("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            Log.severe("Exception {} while trying to write to log file", e);
        } finally {
            buffer.clear();
        }
    }

    private boolean clearOldLogFiles(File[] files) {
        boolean result = true;
        for (int i = 0; i < files.length - MAX_LOG_FILES; i++) {
            File file = files[i];
            if (file != null) {
                result &= file.delete();
            }
        }

        return result;
    }

    public void dipose() {
        Log.fine("Stopping file logger.");
        clear();
        if(future != null) {
            future.cancel(false);
        }
    }
}
