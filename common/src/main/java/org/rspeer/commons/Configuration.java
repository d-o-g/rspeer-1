package org.rspeer.commons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Configuration {

    public static final boolean DEBUG = false;
    public static final String APPLICATION_NAME = "RSPeer";

    public static final String HOME = getSystemHome() + File.separator + APPLICATION_NAME + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String ME = CACHE + "misc";
    public static final String ME_NEW = CACHE + "misc_new";
    public static final String ME_OLD = CACHE + "rspeer_me";

    public static final String DATA = CACHE + "data" + File.separator;

    public static final String OSRS_MAP = CACHE + "runescapeMap.png";
    public static final String MAIN_JAR = CACHE + "rspeer.jar";
    public static final String SCRIPTS = HOME + "scripts" + File.separator;
    public static final String GAMEPACK = CACHE + "gamepack.jar";
    public static final String MODSCRIPT = CACHE + "juice";
    public static final String JSON = CACHE + "json";
    public static final String PREFERENCES_OLD = DATA + "preferences";
    public static final String PREFERENCES = DATA + "preferences.json";

    public static final String[] DIRECTORIES = {CACHE, DATA, SCRIPTS, JSON, JSON + "/objects", JSON + "/varps", JSON + "/items", JSON + "/npcs"};

    public static final String NEW_API_BASE = System.getenv("api_url") != null
            ? System.getenv("api_url") : "https://services.rspeer.org/api/";

    public static final String CLIENT_TAG = "rspeer_client_07";

    static {
        for (String dir : DIRECTORIES) {
            new File(dir).mkdirs();
        }

        try {
            if (!new File(GAMEPACK).exists()) {
                Files.createFile(Paths.get(GAMEPACK));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSystemHome() {
        return OS.get() == OS.WINDOWS ? System.getProperty("user.home") + "/Documents/"
                : System.getProperty("user.home") + "/";
    }
}