package org.rspeer.api_services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.jodah.failsafe.event.ExecutionAttemptedEvent;
import net.jodah.failsafe.function.CheckedConsumer;
import org.json.JSONObject;
import org.rspeer.commons.*;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by MadDev on 2/4/18.
 */
public class RsPeerApi {

    private static final Object lock = new Object();

    public static final Gson gson = new Gson();
    private static String session;
    private static double liveVersion;
    private static double ourVersion;
    private static String ourVersionHash;
    private static JSONObject currentUser;
    private static boolean isInitialized;
    private static String identifier;
    private static CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetryCallback = Failsafe.DEFAULT_ON_RETRY;

    public static void initialize() {
        synchronized (lock) {
            if (isInitialized) {
                return;
            }
            ourVersion = getOurVersion();
            isInitialized = true;
        }
    }

    public static void setOnRetryCallback(CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetryCallback) {
        synchronized (lock) {
            RsPeerApi.onRetryCallback = onRetryCallback;
        }
    }

    public static String getCurrentUserName() {
        synchronized (lock) {
            JSONObject user = getCurrentUser();
            if (user == null) {
                return null;
            }
            return user.getString("username");
        }
    }

    public static int getTotalInstances() {
        synchronized (lock) {
            JSONObject user = getCurrentUser();
            if (user == null) {
                return -1;
            }
            return user.getInt("instances");
        }
    }

    public static String getSession() {
        synchronized (lock) {
            if (session == null) {
                session = Failsafe.execute(s -> AuthorizationService.getInstance().decryptAndGetSession(), 10);
            }
            return session;
        }
    }

    static void setSession(String session) {
        RsPeerApi.session = session;
    }

    public static double getBotVersion() {
        synchronized (lock) {
            if (liveVersion != 0) {
                return liveVersion;
            }
            return Failsafe.execute(s -> {
                HttpResponse<String> version = Unirest.get(Configuration.NEW_API_BASE + "bot/currentVersionRaw").asString();
                System.out.println(version);
                double parsed = MathUtil.round(Double.parseDouble(version.getBody()), 2);
                liveVersion = parsed;
                return parsed;
            });
        }
    }

    public static String getOurVersionHash() {
        return ourVersionHash;
    }

    public static double getOurVersion() {
        synchronized (lock) {
            if (ourVersion != 0) {
                return ourVersion;
            }
            try {
                URI uri = RsPeerApi.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                if (Files.isDirectory(Paths.get(uri))) {
                    uri = Paths.get(Configuration.MAIN_JAR).toUri();
                }
                String mainPath = Paths.get(uri).toString();
                Path path = Paths.get(mainPath);
                if (System.getenv("debug_api") != null) {
                    path = Paths.get(Configuration.MAIN_JAR);
                }
                String botHash = HashUtil.calculateHash(path);
                if (botHash == null) {
                    return ourVersion;
                }
                ourVersionHash = botHash;
                HttpResponse<String> version = Unirest.get(Configuration.NEW_API_BASE + "bot/getVersionByHash?hash=" + botHash).asString();
                return MathUtil.round(Double.parseDouble(version.getBody()), 2);
            } catch (Exception e) {
                Logger.getInstance().capture(e);
            }
            return ourVersion;
        }
    }

    public static void shutDown() {
        synchronized (lock) {
            try {
                PingService.getInstance().onClientClose();
                Unirest.shutdown();
            } catch (Throwable e) {
                if (e.getMessage().contains("Connection pool shut down")) {
                    return;
                }
                e.printStackTrace();
            }
        }
    }

    public static JSONObject getCurrentUser() {
        synchronized (lock) {
            if (currentUser == null) {
                currentUser = Failsafe.execute(s -> UserService.getInstance().getFullUser(), onRetryCallback);
            }
            return currentUser;
        }
    }

    public static int getUserId() {
        synchronized (lock) {
            JSONObject user = getCurrentUser();
            if (user == null) {
                return -1;
            }
            return user.getInt("id");
        }
    }

    public static void log(String type, String message) {
       log(type, message, 50);
    }

    public static void log(String type, String message, int tries) {
        RsPeerExecutor.executeWitRetry(() -> {
            JsonObject o = new JsonObject();
            o.addProperty("type", type);
            o.addProperty("message", message);
            Unirest.post(Configuration.NEW_API_BASE + "user/log")
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .header("Content-Type", "application/json")
                    .body(o.toString())
                    .asString();
        }, tries);
    }

    public static String getIdentifier() {
        synchronized (lock) {
            if(identifier == null) {
                identifier = UUID.randomUUID().toString();
            }
            return identifier;
        }
    }
}
