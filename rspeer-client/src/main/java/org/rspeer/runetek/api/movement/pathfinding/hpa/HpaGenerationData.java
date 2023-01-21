package org.rspeer.runetek.api.movement.pathfinding.hpa;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.api.scene.SceneObjects;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 7/12/2018.
 */
@Deprecated
public class HpaGenerationData {

    private static Set<String> planeChangeNames = new HashSet<>();

    private static Set<String> doorNames = new HashSet<>();
    private static Set<String> doorActions = new HashSet<>();

    private static Set<Position> doorLocationBlacklist = new HashSet<>();
    private static Set<Position> blockedOverride = new HashSet<>();
    private static long lastSend = System.currentTimeMillis();

    static {
        //Plane change names.
        planeChangeNames.add("ladder");
        planeChangeNames.add("stairs");
        planeChangeNames.add("staircase");
        planeChangeNames.add("stairwell");

        //Door names.
        doorNames.add("door");
        doorNames.add("large door");
        doorNames.add("gate");
        doorNames.add("ardougne wall door");

        //Door actions
        doorActions.add("open");
        doorActions.add("close");

        //Black listed door locations.
        blackListDoorLocation(3268, 3227, 0);
        blackListDoorLocation(3268, 3228, 0);

        //Manually blocked locations.
        blockLocation(3258, 3179, 0);
    }

    private static boolean isPlaneChange(String name, String[] actions, Integer objectId) {
        return name != null && planeChangeNames.contains(name.toLowerCase());
    }

    public static boolean isNegativePlaneChange(String name, String[] actions, Integer objectId) {
        return isPlaneChange(name, actions, objectId) && getPlaneChangeAction(false, actions).size() > 0;
    }

    public static boolean isPositivePlaneChange(String name, String[] actions, Integer objectId) {
        return isPlaneChange(name, actions, objectId) && getPlaneChangeAction(true, actions).size() > 0;
    }

    public static Set<String> getPlaneChangeAction(boolean positiveLevelChange, String[] actions) {
        if (positiveLevelChange) {
            return Arrays.stream(actions).filter(s -> s != null && s.toLowerCase().contains("up")).collect(Collectors.toSet());
        }
        return Arrays.stream(actions).filter(s -> s != null && s.toLowerCase().contains("down")).collect(Collectors.toSet());
    }

    public static boolean isPlaneChange(boolean positiveLevelChange, String name, String[] actions, Integer objectId) {
        if (positiveLevelChange) {
            return isPositivePlaneChange(name, actions, objectId);
        }
        return isNegativePlaneChange(name, actions, objectId);
    }

    public static boolean isDoor(Position position, String name, String[] actions, Integer mapDoorFlag) {
        if (position != null && doorLocationBlacklist.contains(position)) return false;
        if (actions == null || Arrays.stream(actions).filter(Objects::nonNull).noneMatch(s -> doorActions.contains(s.toLowerCase()))) return false;
        if (name == null || !doorNames.contains(name.toLowerCase())) return false;
        return mapDoorFlag != null && mapDoorFlag != 0;
    }

    public static void blockLocation(int x, int y, int plane) {
        blockedOverride.add(new Position(x, y, plane));
    }

    public static void blackListDoorLocation(int x, int y, int plane) {
        doorLocationBlacklist.add(new Position(x, y, plane));
    }

    public static boolean isBlocked(int x, int y, int plane) {
        return blockedOverride.contains(new Position(x, y, plane));
    }

    public static Set<String> getPlaneChangeNames() {
        return planeChangeNames;
    }

    public static Set<String> getDoorNames() {
        return doorNames;
    }

    public static Set<String> getDoorActions() {
        return doorActions;
    }

    public static Set<Position> getDoorLocationBlacklist() {
        return doorLocationBlacklist;
    }

    public static Set<Position> getBlockedOverride() {
        return blockedOverride;
    }

    public static void sendCollisionMap() {
        RsPeerExecutor.execute(() -> {
            try {
                if ((System.currentTimeMillis() - lastSend) > TimeUnit.SECONDS.toMillis(10)) {
                    lastSend = System.currentTimeMillis();

                    Position base = Scene.getBase();
                    int[][] collisionFlags = Scene.getCollisionFlags();

                    Set<Map<String, Object>> locations = new HashSet<>();
                    for (SceneObject sceneObject : SceneObjects.getLoaded()) {
                        int id = sceneObject.getId();
                        int orientation = sceneObject.getOrientation();
                        Position position = sceneObject.getPosition();
                        Map<String, Object> location = new HashMap<>();
                        location.put("id", id);
                        location.put("orientation", orientation);
                        location.put("position", position);
                        locations.add(location);
                    }

                    Map<String, Object> send = new HashMap<>();
                    send.put("base", base);
                    send.put("map", collisionFlags);
                    send.put("locations", locations);
                    String json = new Gson().toJson(send);

                    Unirest.put("http://www.web.acuitybotting.com/collision/flags")
                            .body(json)
                            .asStringAsync();
                }
            } catch (Throwable ignored) {
            }
        });
    }

}