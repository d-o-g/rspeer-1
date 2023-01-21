package org.rspeer.runetek.api.commons;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * Created by Spencer on 03/03/2018.
 */
public final class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public <T> T accumulate(BiFunction<L, R, T> function) {
        return function.apply(left, right);
    }

    /**
     * @param predicate The predicate to test the elements
     * @return The pair if the predicate accepts it, else returns a Pair where
     * the left and right elements are null
     */
    public Pair<L, R> filter(BiPredicate<L, R> predicate) {
        if (predicate.test(left, right)) {
            return this;
        }
        return new Pair<>(null, null);
    }

    /**
     * If neither element is null, applies the given consumer
     *
     * @param consumer The operation to perform on the elements
     */
    public void ifPresent(BiConsumer<L, R> consumer) {
        if (left != null && right != null) {
            consumer.accept(left, right);
        }
    }
}
