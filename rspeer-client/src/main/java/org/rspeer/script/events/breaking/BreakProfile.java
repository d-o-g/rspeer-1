package org.rspeer.script.events.breaking;

import com.google.gson.Gson;
import org.rspeer.commons.Configuration;
import org.rspeer.ui.Log;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BreakProfile {

    private static final String BREAK_CACHE = Configuration.DATA + "breaks" + File.separator;
    private static final String BREAK_PROFILE_EXTENSION = ".bp";

    static {
        File cache = new File(BREAK_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
    }

    private final String name;
    private final List<BreakTime> times;
    private final boolean repeat;

    public BreakProfile(String name, List<BreakTime> times, boolean repeat) {
        this.name = name;
        this.times = times;
        this.repeat = repeat;
    }

    public static BreakProfile fromName(String name) throws FileNotFoundException {
        File file = new File(BREAK_CACHE, name + BREAK_PROFILE_EXTENSION);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new Gson();
            return gson.fromJson(reader, BreakProfile.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static String[] listProfileNames() {
        File breakCacheDir = new File(BREAK_CACHE);
        return Stream.of(Objects.requireNonNull(breakCacheDir.list((dir, name)
                -> name.endsWith(BREAK_PROFILE_EXTENSION))))
                .map(str -> str.replace(BREAK_PROFILE_EXTENSION, ""))
                .toArray(String[]::new);
    }

    public String getName() {
        return name;
    }

    public List<BreakTime> getTimes() {
        return times;
    }

    public boolean exists() {
        File file = new File(BREAK_CACHE, name + BREAK_PROFILE_EXTENSION);
        return file.exists();
    }

    public void serialize() {
        File file = new File(BREAK_CACHE, name + BREAK_PROFILE_EXTENSION);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                return;
            }
        }

        Gson gson = new Gson();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            return;
        }
    }

    public static void delete(String name) {
        File file = new File(BREAK_CACHE, name + BREAK_PROFILE_EXTENSION);
        if (file.exists()) {
            file.delete();
        } else {
            Log.severe("Failed to delete break profile!");
        }
    }

    public boolean shouldRepeat() {
        return repeat;
    }
}
