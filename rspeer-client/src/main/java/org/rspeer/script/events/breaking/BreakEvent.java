package org.rspeer.script.events.breaking;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptBlockingEvent;
import org.rspeer.script.ScriptExecutor;
import org.rspeer.ui.Log;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BooleanSupplier;

public class BreakEvent extends ScriptBlockingEvent {

    private static final BooleanSupplier DEFAULT_BREAK_CONDITION = () -> !Players.getLocal().isHealthBarVisible()
            && !Scene.isDynamic() && !Bank.isOpen();

    private static BooleanSupplier breakCondition = DEFAULT_BREAK_CONDITION;

    private BreakProfile profile;
    private StopWatch watch;
    private Queue<BreakTime> times;
    private StopWatch breakWatch;
    private Duration nextBreakTime;
    private Duration nextBreakAt;

    public BreakEvent(Script ctx, BreakProfile profile) {
        super(ctx);
        this.profile = profile;
        if (profile == null) {
            return;
        }

        init();
    }

    public static BooleanSupplier getDefaultCondition() {
        return DEFAULT_BREAK_CONDITION;
    }

    public static void setCondition(BooleanSupplier condition) {
        breakCondition = condition;
    }

    private void init() {
        watch = StopWatch.start();
        times = new LinkedList<>(profile.getTimes());
    }

    @Override
    public boolean validate() {
        if (profile == null || (Game.isLoggedIn() && !breakCondition.getAsBoolean())) {
            return false;
        }

        if (times.isEmpty()) {
            if (profile.shouldRepeat()) {
                init();
            } else {
                ScriptExecutor.stop();
                return false;
            }
        }

        if (breakWatch != null) {
            if (breakWatch.exceeds(nextBreakTime)) {
                breakWatch = null;
                return false;
            } else {
                return true;
            }
        }

        if (nextBreakAt == null) {
            BreakTime next = times.remove();
            long nextSeconds = next.getWaitDuration().toMillis() / 1000;
            double lowBound = nextSeconds * .95;
            double upBound = nextSeconds * 1.05;
            nextBreakAt = Duration.ofSeconds((long) Random.mid(lowBound, upBound));
            long nextBreakSeconds = (long) Random.mid(next.getBreakDurationSeconds() * .95,
                    next.getBreakDurationSeconds() * 1.05);
            nextBreakTime = Duration.ofSeconds(nextBreakSeconds);
            Log.fine("Taking next break at " + Time.format(nextBreakAt));
        }

        if (watch.exceeds(nextBreakAt)) {
            nextBreakAt = null;
            breakWatch = StopWatch.start();
            Log.fine("Going to take a break now for " + Time.format(nextBreakTime));
            return true;
        }

        return false;
    }

    @Override
    public void process() {
        if (!Game.isLoggedIn()) {
            Time.sleep(1000);
            return;
        }
        Game.logout();
        Time.sleepUntil(() -> !Game.isLoggedIn(), 1200);
    }
}
