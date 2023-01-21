package org.rspeer.script.events.breaking;

import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.math.Random;

import java.time.Duration;
import java.util.Arrays;

public class BreakTime implements Comparable<BreakTime> {

    public static final int SECONDS_PER_HOUR = 3600;
    public static final int SECONDS_PER_MINUTE = 60;

    private static final int MAX_MINUTES_SECONDS = 59;
    private static final int BREAK_TIME_RATIO_LOWER_BOUND = 3;
    private static final int BREAK_TIME_RATIO_UPPER_BOUND = 15;

    private final Duration waitDuration;
    private final long hours;
    private final long minutes;
    private final long seconds;

    private final long breakDurationSeconds;

    public BreakTime(long hours, long minutes, long seconds, long breakDurationSeconds) {
        this.waitDuration = Duration.ofHours(hours)
                .plus(Duration.ofMinutes(minutes))
                .plus(Duration.ofSeconds(seconds));

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

        this.breakDurationSeconds = breakDurationSeconds;
    }

    public static BreakTime random(long sdHours, long sdMinutes, long sdSeconds) {
        return random(null, sdHours, sdMinutes, sdSeconds);
    }

    public static BreakTime random(BreakTime from, long sdHours, long sdMinutes, long sdSeconds) {
        long startH = from != null ? from.getWaitDuration().plus(from.getBreakDuration()).toHours() : 0;

        long secondsTotal = sdHours * SECONDS_PER_HOUR + sdMinutes * SECONDS_PER_MINUTE + sdSeconds;
        long randomSeconds = (long) Random.mid(secondsTotal / 8.0, secondsTotal * 2);

        long rndH = randomSeconds / SECONDS_PER_HOUR;
        long rndM = (randomSeconds - (rndH * SECONDS_PER_HOUR)) / SECONDS_PER_MINUTE;
        long rndS = randomSeconds - (rndH * SECONDS_PER_HOUR) - (rndM * SECONDS_PER_MINUTE);

        long diff = rndH * SECONDS_PER_HOUR + rndM * SECONDS_PER_MINUTE + rndS;

        long breakDurationSeconds = diff / Random.nextInt(BREAK_TIME_RATIO_LOWER_BOUND, BREAK_TIME_RATIO_UPPER_BOUND);

        return new BreakTime(
                startH + rndH,
                Math.min(rndM, MAX_MINUTES_SECONDS),
                Math.min(rndS, MAX_MINUTES_SECONDS),
                breakDurationSeconds
        );
    }

    public boolean elapsed(StopWatch time) {
        return time.getElapsed().compareTo(waitDuration) >= 0;
    }

    public long getWaitHours() {
        return hours;
    }

    public long getWaitMinutes() {
        return minutes;
    }

    public long getWaitSeconds() {
        return seconds;
    }

    public long getBreakDurationSeconds() {
        return breakDurationSeconds;
    }

    public long getTotalWaitSeconds() {
        return getWaitDuration().getSeconds();
    }

    public Duration getWaitDuration() {
        return Duration.ofHours(getWaitHours())
                .plus(Duration.ofMinutes(getWaitMinutes()))
                .plus(Duration.ofSeconds(getWaitSeconds()));
    }

    public Duration getBreakDuration() {
        return Duration.ofSeconds(getBreakDurationSeconds());
    }

    public boolean before(BreakTime other) {
        return other.getTotalWaitSeconds() > getTotalWaitSeconds();
    }

    public String toString() {
        return String.format("BreakTime[%02d:%02d:%02d for %d seconds]",
                getWaitHours(),
                getWaitMinutes(),
                getWaitSeconds(),
                getBreakDurationSeconds()
        );
    }

    public String getBreakString() {
        return String.format("%02d:%02d:%02d", getWaitHours(), getWaitMinutes(), getWaitSeconds());
    }

    @Override
    public int compareTo(BreakTime o) {
        return Long.compare(getTotalWaitSeconds(), o.getTotalWaitSeconds());
    }
}
