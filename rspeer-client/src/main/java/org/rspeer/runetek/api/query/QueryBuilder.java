package org.rspeer.runetek.api.query;

import org.rspeer.runetek.api.query.results.QueryResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class QueryBuilder<T, QB extends QueryBuilder, QR extends QueryResults> implements Cloneable, Predicate<T> {

    private Supplier<List<? extends T>> provider;
    private Predicate<? super T> customFilter;

    public abstract Supplier<List<? extends T>> getDefaultProvider();

    public QR results() {
        List<? extends T> data = provider != null ? provider.get() : getDefaultProvider().get();
        List<T> filtered = new ArrayList<>();
        for (T elem : data) {
            if (test(elem)) {
                filtered.add(elem);
            }
        }
        return createQueryResults(filtered);
    }

    public final QB filter(Predicate<? super T> filter) {
        if (customFilter != null) {
            Predicate<? super T> old = customFilter;
            customFilter = (Predicate<T>) t -> old.test(t) && filter.test(t);
        } else {
            customFilter = filter;
        }
        return self();
    }

    public QB clone() {
        try {
            return (QB) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return self();
    }

    public final QB provider(Supplier<List<? extends T>> provider) {
        this.provider = provider;
        return self();
    }

    protected final QB self() {
        return (QB) this;
    }

    protected abstract QR createQueryResults(Collection<? extends T> raw);

    @Override
    public boolean test(T t) {
        return customFilter == null || customFilter.test(t);
    }
}
