package org.rspeer.runetek.api.commons;

public final class ArrayUtils {

    private ArrayUtils() {
        throw new IllegalAccessError();
    }

    public static <T> boolean contains(T[] array, T value) {
        for (T elem : array) {
            if (elem.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] array, int value) {
        for (int elem : array) {
            if (elem == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsExact(String[] array, String value) {
        for (String elem : array) {
            if (elem.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsExactInsensitive(String[] array, String value) {
        for (String elem : array) {
            if (elem.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
