package org.rspeer.runetek.api.movement;

import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.RemoteBotPreferenceService;
import org.rspeer.networking.dax.walker.DaxServer;
import org.rspeer.networking.dax.walker.DaxWalker;
import org.rspeer.networking.dax.walker.engine.definitions.WalkCondition;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.debug.MovementDebug;
import org.rspeer.runetek.api.movement.path.BresenhamPath;
import org.rspeer.runetek.api.movement.path.DaxPath;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.executor.custom.CustomPathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.region.astar.AStar;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Reachable;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptExecutor;
import org.rspeer.ui.Log;

import java.util.Objects;
import java.util.function.Function;

public final class Movement {

    private static final MovementDebug debug = new MovementDebug();
    private static final Varpbit STAMINA_ENHANCEMENT_VARPBIT = Varps.getBit(25);
    private static final int RUN_VARP = 173;
    private static DaxWalker daxWalker = new DaxWalker(new DaxServer());

    private static Function<Position, Reachable> reachableProvider = position -> new Reachable().build(position);
    private static Reachable cachedReachable = new Reachable();

    private static CustomPathExecutor customPathExecutor = new CustomPathExecutor();

    public static CustomPathExecutor getCustomPathExecutor() {
        return customPathExecutor;
    }

    private Movement() {
        throw new IllegalAccessError();
    }

    public static int getPlayerWeight() {
        return Game.getClient().getWeight();
    }

    public static DaxWalker getDaxWalker() {
        return daxWalker;
    }

    /**
     * @return {@code null} if no destination is set, or the destination tile
     */
    public static Position getDestination() {
        if (!isDestinationSet()) {
            return null;
        }
        return new Position(Scene.getBaseX() + Game.getClient().getDestinationX(), Scene.getBaseY() + Game.getClient().getDestinationY(), Scene.getFloorLevel());
    }

    public static boolean isDestinationSet() {
        return Game.getClient().getDestinationX() > 0;
    }

    /**
     * @return The distance to the destination tile, {@code Integer.MAX_VALUE} if no destination
     */
    public static double getDestinationDistance() {
        Position dest = getDestination();
        return dest != null ? dest.distance() : Integer.MAX_VALUE;
    }

    public static void setWalkFlag(Positionable destination) {
        int x = destination.getPosition().getX() - Scene.getBaseX();
        int y = destination.getPosition().getY() - Scene.getBaseY();

        if (x < 0) {
            x = 0;
        }
        if (x > 104) {
            x = 104;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > 104) {
            y = 104;
        }

        Game.getClient().setViewportWalking(true);
        Game.getClient().setSelectedRegionTileX(x);
        Game.getClient().setSelectedRegionTileY(y);
    }

    public static boolean setWalkFlagWithConfirm(Positionable destination) {
        Position destinationPosition = destination.getPosition();

        int x = destinationPosition.getX() - Scene.getBaseX();
        int y = destinationPosition.getY() - Scene.getBaseY();
        int floorLevel = destinationPosition.getFloorLevel();

        if (x < 0 || x > 104 || y < 0 || y > 104 || floorLevel != Scene.getFloorLevel()) {
            return false;
        }

        Game.getClient().setViewportWalking(true);
        Game.getClient().setSelectedRegionTileX(x);
        Game.getClient().setSelectedRegionTileY(y);

        Time.sleep(70, 100);
        return destinationPosition.equals(getDestination());
    }

    public static boolean isRunEnabled() {
        return Varps.getBoolean(RUN_VARP);
    }

    public static int getRunEnergy() {
        return Game.getClient().getEnergy();
    }

    public static boolean isStaminaEnhancementActive() {
        return STAMINA_ENHANCEMENT_VARPBIT.booleanValue();
    }

    public static boolean toggleRun(boolean on) {
        if (Movement.isRunEnabled() == on) {
            return true;
        }
        InterfaceComponent btn = Interfaces.getComponent(160, 23);
        return btn != null && btn.interact(x -> true);
    }

    public static boolean walkTo(Positionable destination) {
        return walkTo(destination, PathExecutor.getPathExecutorSupplier().get());
    }

    public static boolean walkTo(Positionable destination, WalkCondition condition) {
        return walkTo(destination, PathExecutor.getPathExecutorSupplier().get(), condition);
    }

    public static boolean walkToRandomized(Positionable destination) {
        return walkTo(destination, PathExecutor.getPathExecutorSupplier().get().setRandomizeAll(true));
    }

    public static boolean walkTo(Positionable destination, PathExecutor pathExecutor) {
        return walkTo(destination, pathExecutor, null);
    }

    public static boolean walkTo(Positionable destination, PathExecutor pathExecutor, WalkCondition condition) {
        Path path = buildPath(destination, condition);
        return path != null && pathExecutor.execute(path);
    }

    public static Path buildPath(Positionable destination) {
        return buildPath(Players.getLocal(), destination, null);
    }

    public static Path buildPath(Positionable destination, WalkCondition condition) {
        return buildPath(Players.getLocal(), destination, condition);
    }

    public static Path buildPath(Positionable start, Positionable destination, boolean enableCustomPaths) {
        return enableCustomPaths ? buildPath(start, destination) : buildPathInternal(start, destination);
    }

    public static Path buildPath(Positionable start, Positionable destination) {
       return buildPath(start, destination, null);
    }

    public static Path buildPath(Positionable start, Positionable destination, WalkCondition condition) {
        if (start == null || destination == null) {
            return null;
        }
        Path custom = customPathExecutor.execute(start, destination);
        return custom != null ? custom : buildPathInternal(start, destination, condition);
    }

    public static Path buildPathInternal(Positionable start, Positionable destination) {
        return buildPathInternal(start, destination, null);
    }

    public static Path buildPathInternal(Positionable start, Positionable destination, WalkCondition condition) {
        Position startPosition = start.getPosition();
        Position endPosition = destination.getPosition();

        if (startPosition == null || endPosition == null) {
            return null;
        }

        WebWalker walker = getDefaultWebWalker();

        if(walker == WebWalker.ClientSettingsBased) {
            try {
                BotPreferences preferences = RemoteBotPreferenceService.get();
                walker = WebWalker.values()[preferences.getWebWalker()];
            } catch (Exception e) {
                walker = WebWalker.Acuity;
            }
        }

        //DaxWeb
        if(walker == WebWalker.Dax) {
            return new DaxPath(daxWalker, endPosition, condition);
        }

        Path result = HpaPath.build(startPosition, endPosition);

        if (result == null) {
            if (Scene.isLoaded(startPosition) && Scene.isLoaded(endPosition) && startPosition.getFloorLevel() == endPosition.getFloorLevel()) {
                result = new AStar().buildPath(startPosition, endPosition);
            }
        }

        if (result == null && startPosition.getFloorLevel() == endPosition.getFloorLevel()) {
            result = BresenhamPath.build(startPosition, endPosition);
        }

        if (getDebug().isToggled()) {
            Log.info("Movement.buildPath result: " + result);
            getDebug().setLastBuiltPath(result);
        }

        return result;
    }

    public static Reachable getReachableMap() {
        return getReachableMap(Players.getLocal());
    }

    public static Reachable getReachableMap(Positionable start) {
        Objects.requireNonNull(start);
        Position cachedStart = cachedReachable.getStart();
        Position startPosition = start.getPosition();
        if (startPosition.equals(cachedStart) && Scene.getLastCollisionUpdate() < cachedReachable.getBuildTime()) {
            return cachedReachable;
        }
        return cachedReachable = reachableProvider.apply(startPosition);
    }

    public static boolean isWalkable(Positionable destination) {
        return isWalkable(destination, true);
    }

    public static boolean isWalkable(Positionable destination, boolean acceptEndBlocked) {
        return getReachableMap(Players.getLocal()).isReachable(destination, acceptEndBlocked);
    }

    public static boolean isInteractable(Positionable destination) {
        return isInteractable(destination, true);
    }

    public static boolean isInteractable(Positionable destination, boolean acceptEndBlocked) {
        Reachable reachableMap = getReachableMap(Players.getLocal());
        return reachableMap.isReachable(destination, acceptEndBlocked) && !reachableMap.isActionRequired(destination);
    }

    public static MovementDebug getDebug() {
        return debug;
    }

    public static void setReachableProvider(Function<Position, Reachable> reachableFunction) {
        Movement.reachableProvider = reachableFunction;
    }

    /**
     * @apiNote Sets the default WebWalker to use for the entire lifetime of this specific script run.
     * @apiNote Gets reset back to ClientSettingBased once the script ends.
     * @param walker
     */
    public static void setDefaultWebWalker(WebWalker walker) {
        Script script = ScriptExecutor.getCurrent();
        if(script != null) {
            script.getConfiguration().setWalker(walker);
        }
    }

    /**
     * @return The WebWalker to use for the entire lifetime of this specific script run.
     */
    public static WebWalker getDefaultWebWalker() {
        Script script = ScriptExecutor.getCurrent();
        if(script != null) {
            return script.getConfiguration().getWalker();
        }
        return WebWalker.ClientSettingsBased;
    }
}
