package org.rspeer.networking.acuity;

import org.rspeer.networking.acuity.services.path_finding.HpaService;
import org.rspeer.networking.acuity.services.player_cache.PlayerCacheService;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class AcuityServices {

    private static boolean debug = System.getenv("acuity-debug") != null;

    public static void start() {
        PlayerCacheService.start();
        HpaService.start();
    }

    public static void onException(Throwable throwable) {
        if (debug && throwable != null) {
            throwable.printStackTrace();
        }
    }

    public static void log(String log) {
        if (debug) {
            System.out.println(log);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        AcuityServices.debug = debug;
    }
}
