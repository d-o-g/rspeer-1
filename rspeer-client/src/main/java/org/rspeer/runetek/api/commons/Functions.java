package org.rspeer.runetek.api.commons;

import org.rspeer.runetek.adapter.Adapter;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSProvider;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class Functions {

    private Functions() {
        throw new IllegalAccessError();
    }

    /**
     * @param supplier The supplier
     * @param function The function to apply
     * @param fallback The value to return if the supplied arg is null
     * @param <T>      argument type
     * @param <R>      return type
     * @return Applies a single argument function to the supplied argument
     * and returns the result if the arg is not null, else returns the fallback value
     */
    public static <T, R> R mapOrDefault(Supplier<T> supplier, Function<T, R> function, R fallback) {
        T value = supplier.get();
        return value != null ? function.apply(value) : fallback;
    }

    public static <T, R> R mapOrNull(Supplier<T> supplier, Function<T, R> function) {
        return mapOrDefault(supplier, function, null);
    }

    public static <T> boolean mapOrElse(Supplier<T> supplier, ToBooleanFunction<T> function, boolean fallback) {
        T value = supplier.get();
        return value != null ? function.applyAsBoolean(value) : fallback;
    }

    public static <T> boolean mapOrElse(Supplier<T> supplier, ToBooleanFunction<T> function) {
        return mapOrElse(supplier, function, false);
    }

    public static <T> int mapOrDefault(Supplier<T> supplier, ToIntFunction<T> function, int fallback) {
        T value = supplier.get();
        return value != null ? function.applyAsInt(value) : fallback;
    }

    public static <T> int mapOrM1(Supplier<T> supplier, ToIntFunction<T> function) {
        return mapOrDefault(supplier, function, -1);
    }

    public static <A extends Adapter, P extends RSProvider> A wrapOrDefault(Supplier<P> providerSupplier, Function<P, A> wrapper, A fallback) {
        P provider = providerSupplier.get();
        return provider != null ? wrapper.apply(provider) : fallback;
    }

    public static <T> void ifPresent(Supplier<T> supplier, Consumer<T> consumer) {
        T value = supplier.get();
        if (value != null) {
            consumer.accept(value);
        }
    }
}
