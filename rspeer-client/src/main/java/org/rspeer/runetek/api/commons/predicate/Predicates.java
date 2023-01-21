package org.rspeer.runetek.api.commons.predicate;

import java.util.*;
import java.util.function.Predicate;

public final class Predicates {

    private static final Predicate ALWAYS = t -> true;
    private static final Predicate NEVER = ALWAYS.negate();

    private Predicates() {
        throw new IllegalAccessError();
    }

    public static <T> Predicate<T> always() {
        return ALWAYS;
    }

    public static <T> Predicate<T> never() {
        return NEVER;
    }

    public static <T> T firstMatching(Predicate<? super T> predicate, T... elems) {
        if (elems == null) {
            return null;
        }

        for (T elem : elems) {
            if (elem != null && predicate.test(elem)) {
                return elem;
            }
        }
        return null;
    }

    public static <T> boolean matching(Predicate<? super T> predicate, T... elems) {
        return firstMatching(predicate, elems) != null;
    }

    public static <T> T firstMatching(Predicate<? super T> predicate, Iterable<T> elems) {
        if (elems == null) {
            return null;
        }

        for (T elem : elems) {
            if (elem != null && predicate.test(elem)) {
                return elem;
            }
        }
        return null;
    }

    public static <T> boolean matching(Predicate<? super T> predicate, Iterable<T> elems) {
        return firstMatching(predicate, elems) != null;
    }

    public static <T> List<T> allMatching(Predicate<? super T> predicate, Iterable<T> elems) {
        List<T> match = new ArrayList<>();
        if (elems == null) {
            return match;
        }

        for (T elem : elems) {
            if (predicate.test(elem)) {
                match.add(elem);
            }
        }
        return match;
    }

    public static <T> List<T> allMatching(Predicate<? super T> predicate, T... elems) {
        List<T> match = new ArrayList<>();
        if (elems == null) {
            return match;
        }

        for (T elem : elems) {
            if (predicate.test(elem)) {
                match.add(elem);
            }
        }
        return match;
    }

    public static <T> Predicate<T> or(Predicate<T> a, Predicate<T> b) {
        return t -> a.test(t) || b.test(t);
    }

    public static <T> Predicate<T> and(Predicate<T> a, Predicate<T> b) {
        return t -> a.test(t) && b.test(t);
    }
}
