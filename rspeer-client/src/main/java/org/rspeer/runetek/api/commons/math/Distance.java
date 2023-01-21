package org.rspeer.runetek.api.commons.math;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;

import java.util.Arrays;
import java.util.function.Predicate;

public enum Distance implements DistanceEvaluator {

    EUCLIDEAN {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.hypot(x2 - x1, y2 - y1);
        }
    },

    EUCLIDEAN_SQUARED {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.sqrt(Math.pow(x2 - x1, 2)) + Math.sqrt(Math.pow(y2 - y1, 2));
        }
    },

    MANHATTAN {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.abs(x2 - x1) + Math.abs(y2 - y1);
        }
    },

    CHEBYSHEV {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        }
    };

    private static final DistanceEvaluator DEFAULT_EVALUATOR = Distance.EUCLIDEAN_SQUARED;

    public static DistanceEvaluator getDefaultEvaluator() {
        return DEFAULT_EVALUATOR;
    }

    public static <T extends Positionable> T getNearest(T[] array, Positionable source, Predicate<T> predicate) {
        double distance = Integer.MAX_VALUE;
        T closest = null;
        for (T entity : array) {
            if (predicate.test(entity) && entity.getPosition().distance(source) < distance) {
                closest = entity;
                distance = entity.getPosition().distance(source);
            }
        }
        return closest;
    }

    public static <T extends Positionable> T getNearest(T[] array, Predicate<T> predicate) {
        return getNearest(array, Players.getLocal(), predicate);
    }

    public static <T extends Positionable> T[] sort(T[] original, DistanceEvaluator evaluator, boolean ascending) {
        Arrays.sort(original, (T o1, T o2) -> (int) ((ascending ? 1 : -1) * evaluator.evaluate(o1, o2)));
        return original;
    }

    public static <T extends Positionable> T[] sort(T[] original, DistanceEvaluator evaluator) {
        return sort(original, evaluator, true);
    }

    public static <T extends Positionable> T[] sort(T[] original) {
        return sort(original, Distance.getDefaultEvaluator());
    }

    /**
     * @param from The source
     * @param to   The destination
     * @return The distance between the 2 entities using the default evaluator
     */
    public static double between(Positionable from, Positionable to) {
        if (from == null || to == null) {
            return Double.MAX_VALUE;
        }
        return evaluate(DEFAULT_EVALUATOR, from, to);
    }

    /**
     * Evalues the distance to a positionable
     *
     * @param to The destination
     * @return The distance between the local player and the destination
     */
    public static double to(Positionable to) {
        if (to == null) {
            return Double.MAX_VALUE;
        }

        return evaluate(DEFAULT_EVALUATOR, Players.getLocal(), to);
    }

    public static double evaluate(DistanceEvaluator algo, Positionable from, Positionable to) {
        Position w1 = from.getPosition();
        Position w2 = to.getPosition();
        return algo.evaluate(w1.getX(), w1.getY(), w2.getX(), w2.getY());
    }

    public static double evaluate(DistanceEvaluator algo, int x1, int y1, int x2, int y2) {
        return algo.evaluate(x1, y1, x2, y2);
    }

    public abstract double evaluate(int x1, int y1, int x2, int y2);
}
