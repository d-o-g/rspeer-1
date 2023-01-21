package org.rspeer.commons;

import java.io.File;

public enum OS {

    WINDOWS,
    MAC,
    LINUX,
    UNKNOWN;

    public static String getHomeDirectory() {
        switch (get()) {
            case WINDOWS: {
                return System.getProperty("user.home") + File.separator;
            }
            default: {
                return File.separator + "home" + File.separator;
            }
        }
    }

    public static OS get() {
        String os = System.getProperty("os.name");
        for (OS o : OS.values()) {
            if (os.contains(o.toString())) {
                return o;
            }
        }

        return UNKNOWN;
    }

    @Override
    public String toString() {
        String orig = super.toString();
        return Character.toUpperCase(orig.charAt(0)) + orig.substring(1).toLowerCase();
    }
}