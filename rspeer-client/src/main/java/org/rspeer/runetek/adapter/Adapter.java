package org.rspeer.runetek.adapter;

import org.rspeer.runetek.providers.RSProvider;

import java.util.function.Function;

public abstract class Adapter<P extends RSProvider, S extends Adapter<P, S>> {

    protected final P provider;

    protected Adapter(P provider) {
        this.provider = provider;
    }

    /**
     * Applies the provided mapping function to this adapter
     *
     * @param <R>    The type of the result of the mapping function
     * @param mapper a mapping function to apply to the adapter
     * @return an object describing the result of applying a mapping
     * function to the value of this {@code Adapter}
     */
    public <R> R map(Function<S, R> mapper) {
        return mapper.apply((S) this);
    }

    /**
     * @return The {@code RSProvider} object, held by this {@code Adapter}
     */
    public final P getProvider() {
        return provider;
    }
}
