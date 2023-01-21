package org.rspeer.commons;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@DoNotRename
public final class BotPreferences {

    private static final Object lock = new Object();
    @DoNotRename
    private static BotPreferences INSTANCE;
    @DoNotRename
    private static Gson gson;
    @DoNotRename
    private boolean localScriptsOnly;
    @DoNotRename
    private boolean enableFileLogging;
    @DoNotRename
    private boolean expandLogger;
    @DoNotRename
    private boolean closeOnBan;
    @DoNotRename
    private boolean showIpOnMenuBar = true;
    @DoNotRename
    private boolean showAccountOnMenuBar = true;
    @DoNotRename
    private boolean showScriptOnMenuBar = true;
    @DoNotRename
    private boolean allowScriptMessageOnMenuBar = true;
    @DoNotRename
    private int webWalker = 1;
    @DoNotRename
    private boolean festiveMode = true;

    private BotPreferences() {
        gson = new Gson();
    }

    @DoNotRename
    public static BotPreferences load() {
        return getInstance();
    }

    @DoNotRename
    public static BotPreferences getInstance() {
        synchronized (lock) {
            if (INSTANCE != null) {
                return INSTANCE;
            }
            BotPreferences defaultInstance = new BotPreferences();
            try {
                if (Files.exists(Paths.get(Configuration.PREFERENCES_OLD))) {
                    defaultInstance.loadOldPreferences();
                    Files.deleteIfExists(Paths.get(Configuration.PREFERENCES_OLD));
                    defaultInstance.save();
                }
                Path preferences = Paths.get(Configuration.PREFERENCES);
                if (!Files.exists(preferences)) {
                    defaultInstance.save();
                }
                String json = new String(Files.readAllBytes(preferences));
                INSTANCE = gson.fromJson(json, BotPreferences.class);
                return INSTANCE;
            } catch (Throwable e) {
                try {
                    Files.deleteIfExists(Paths.get(Configuration.PREFERENCES));
                    BotPreferences clean = new BotPreferences();
                    clean.save();
                    INSTANCE = clean;
                    return INSTANCE;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            return defaultInstance;
        }
    }

    private void loadOldPreferences() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(Configuration.PREFERENCES_OLD))) {
            localScriptsOnly = in.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void save() {
        synchronized (lock) {
            String payload = gson.toJson(this);
            Path preferences = Paths.get(Configuration.PREFERENCES);
            try {
                if(!Files.exists(preferences)) {
                    Files.createFile(preferences);
                }
                Files.write(Paths.get(Configuration.PREFERENCES), payload.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                RemoteBotPreferenceService.save(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @DoNotRename
    public boolean isLocalScriptsOnly() {
        return localScriptsOnly;
    }

    @DoNotRename
    public void setLocalScriptsOnly(boolean localScriptsOnly) {
        this.localScriptsOnly = localScriptsOnly;
        this.save();
    }

    @DoNotRename
    public void setEnableFileLogging(boolean enableFileLogging) {
        this.enableFileLogging = enableFileLogging;
        this.save();
    }

    @DoNotRename
    public void setExpandLogger(boolean expandLogger) {
        this.expandLogger = expandLogger;
        this.save();
    }

    @DoNotRename
    public boolean isCloseOnBan() {
        return closeOnBan;
    }

    @DoNotRename
    public void setCloseOnBan(boolean closeOnBan) {
        this.closeOnBan = closeOnBan;
        this.save();
    }

    @DoNotRename
    public boolean isShowIpOnMenuBar() {
        return showIpOnMenuBar;
    }

    @DoNotRename
    public void setShowIpOnMenuBar(boolean showIpOnMenuBar) {
        this.showIpOnMenuBar = showIpOnMenuBar;
        this.save();
    }

    @DoNotRename
    public void setFestiveMode(boolean festiveMode) {
        this.festiveMode = festiveMode;
        this.save();
    }

    @DoNotRename
    public void setWebWalker(int webWalker) {
        this.webWalker = webWalker;
        this.save();
    }

    @DoNotRename
    public boolean isFestiveMode() {
        return festiveMode;
    }

    @DoNotRename
    public int getWebWalker() {
        return webWalker;
    }

    @DoNotRename
    public boolean isShowAccountOnMenuBar() {
        return showAccountOnMenuBar;
    }

    @DoNotRename
    public boolean isShowScriptOnMenuBar() {
        return showScriptOnMenuBar;
    }

    @DoNotRename
    public void setShowAccountOnMenuBar(boolean showAccountOnMenuBar) {
        this.showAccountOnMenuBar = showAccountOnMenuBar;
        this.save();
    }

    @DoNotRename
    public void setShowScriptOnMenuBar(boolean showScriptOnMenuBar) {
        this.showScriptOnMenuBar = showScriptOnMenuBar;
        this.save();
    }

    @DoNotRename
    public boolean isAllowScriptMessageOnMenuBar() {
        return allowScriptMessageOnMenuBar;
    }

    @DoNotRename
    public void setAllowScriptMessageOnMenuBar(boolean allowScriptMessageOnMenuBar) {
        this.allowScriptMessageOnMenuBar = allowScriptMessageOnMenuBar;
        this.save();
    }

    @DoNotRename
    public boolean isExpandLogger() {
        return expandLogger;
    }

    @DoNotRename
    public boolean isEnableFileLogging() {
        return enableFileLogging;
    }

}
