package org.rspeer.runetek.api.commons.math;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public final class Random {

    private Random() {
        throw new IllegalAccessError();
    }

    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int nextInt(int max) {
        return nextInt(0, max);
    }

    public static int nextInt(int min, int max) {
        return min == max ? min : ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double nextDouble(double max) {
        return nextDouble(0, max);
    }

    public static double nextDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static <T> T nextElement(T[] elements) {
        return elements.length == 0 ? null : elements[nextInt(elements.length)];
    }

    /**
     * @return A random number from the given array, or -1 if the array is empty
     */
    public static int nextElement(int[] elements) {
        return elements.length == 0 ? -1 : elements[nextInt(elements.length)];
    }

    @SuppressWarnings("unchecked")
    public static <T> T nextElement(Collection<T> elements) {
        Object[] array = elements.toArray();
        return (T) nextElement(array);
    }

    public static double nextGaussian() {
        return ThreadLocalRandom.current().nextGaussian();
    }

    public static double mid(double min, double max) {
        double r = max - min;
        double m = r / 2;
        double s = Random.nextDouble(0, m);
        int sign = Random.nextBoolean() ? -1 : 1;
        return min + (m + (sign * (m - Math.sqrt(m * m - s * s))));
    }

    public static int mid(int min, int max) {
        int r = max - min;
        int m = r / 2;
        int s = Random.nextInt(0, m);
        int sign = Random.nextBoolean() ? -1 : 1;
        return (int) (min + m + (sign * (m - Math.sqrt(m * m - s * s))));
    }

    public static double polar(double min, double max) {
        double r = max - min;
        double m = r / 2;
        double s = Random.nextDouble(0, m);
        int sign = Random.nextBoolean() ? -1 : 1;
        return m + sign * Math.sqrt(m * m - s * s);
    }

    /**
     * High probability: Low + High Ends of the range
     * Low probability:  Mid point of the range
     * [ ***********......     .....********** ]
     */
    public static int polar(int min, int max) {
        int r = max - min;
        int m = r / 2;
        int s = Random.nextInt(0, m);
        int sign = Random.nextBoolean() ? -1 : 1;
        return m + (int) (sign * Math.sqrt(m * m - s * s));
    }

    /**
     * [ .................     .....********** ]
     */
    public static int high(int min, int max) {
        int r = max - min;
        int s = Random.nextInt(0, r);
        return (int) (min + Math.sqrt(r * r - s * s));
    }

    /**
     * [ **********.....     ................. ]
     */
    public static int low(int min, int max) {
        int r = max - min;
        int s = Random.nextInt(0, r);
        return (int) (max - Math.sqrt(r * r - s * s));
    }

    public static double high(double min, double max) {
        double r = max - min;
        double s = Random.nextDouble(0, r);
        return (min + Math.sqrt(r * r - s * s));
    }

    public static double low(double min, double max) {
        double r = max - min;
        double s = Random.nextDouble(0, r);
        return (max - Math.sqrt(r * r - s * s));
    }

    public static Point nextPoint(Rectangle r) {
        int rx = r.x + nextInt(0, r.width);
        int ry = r.y + nextInt(0, r.height);
        return new Point(rx, ry);
    }

    public static Point mid(Rectangle r) {
        return mid(r.x, r.y, r.width, r.height);
    }

    public static Point mid(int x, int y, int w, int h) {
        int rx = x + mid(0, w);
        int ry = y + mid(0, h);
        return new Point(rx, ry);
    }

    public static long nextLong(long max) {
        return nextLong(0, max);
    }

    public static long nextLong(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }
}
