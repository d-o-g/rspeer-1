package org.rspeer.networking.acuity.services.path_finding;

import com.mashape.unirest.http.Unirest;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.networking.acuity.AcuityServices;
import org.rspeer.networking.acuity.services.path_finding.domain.PathRequest;
import org.rspeer.networking.acuity.services.path_finding.domain.PathResult;
import org.rspeer.networking.acuity.services.player_cache.domain.CachedPlayer;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.movement.WebWalker;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.hpa.cache.AbstractHpaCache;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;
import org.rspeer.runetek.event.types.BotCommandEvent;
import org.rspeer.ui.Log;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 7/11/2018.
 */
public class HpaService {

    private static HpaCache hpaCache = new HpaCache();
    private static boolean scannerStarted = false;

    public static void start() {
        AbstractHpaCache.setInstance(hpaCache);
        startScanner();
    }

    private static PathResult getPath(PathRequest request) {
        try {
            String body = Unirest.post(Configuration.NEW_API_BASE + "walker/generatePaths")
                    .queryString("walker", WebWalker.Acuity)
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .body(RSPeer.gson.toJson(request))
                    .asString()
                    .getBody();
            return RSPeer.gson.fromJson(body, PathResult.class);
        } catch (Throwable e) {
            Log.severe("Failed to generate web path.");
            Log.severe(e);
            AcuityServices.onException(e);
        }
        return null;
    }

    static HpaPath findPath(Positionable start, Positionable end, CachedPlayer player) {
        if (start.equals(end)) {
            return null;
        }

        PathRequest pathRequest = new PathRequest();
        pathRequest.setStart(new ArrayList<>());
        pathRequest.getStart().add(new HpaLocation(start.getX(), start.getY(), start.getFloorLevel()));
        pathRequest.setEnd(new ArrayList<>());
        pathRequest.getEnd().add(new HpaLocation(end.getX(), end.getY(), end.getFloorLevel()));
        pathRequest.setPlayer(player);

        PathResult pathResult = getPath(pathRequest);

        if (pathResult == null || pathResult.getPath() == null || pathResult.getPath().size() == 0) {
            return null;
        }

        HpaPath hpaPath = new HpaPath();
        hpaPath.setDestination(end.getPosition());
        hpaPath.setPath(pathResult.getPath());
        hpaPath.setSubPaths(pathResult.getSubPaths());
        return hpaPath;
    }

    private static void startScanner() {
        if (!scannerStarted) {
            scannerStarted = true;
            final Scanner scanner = new Scanner(System.in);
            RsPeerExecutor.scheduleAtFixedRate(() -> {
                try {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        Game.getEventDispatcher().immediate(new BotCommandEvent(line));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, 0, 600, TimeUnit.MILLISECONDS);
        }

    }

    public static HpaCache getHpaCache() {
        return hpaCache;
    }
}
