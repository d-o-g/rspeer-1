package org.rspeer.runetek.api.query.results;

import org.rspeer.runetek.api.commons.math.Random;

import java.util.*;
import java.util.function.Function;

public abstract class QueryResults<T, QR extends QueryResults> implements Collection<T> {

    protected final List<T> results;

    public QueryResults(Collection<? extends T> results) {
        if (results instanceof List) {
            this.results = (List<T>) results;
        } else {
            this.results = new ArrayList<>(results);
        }
    }

    public final QR sort(Comparator<? super T> comparator) {
        results.sort(comparator);
        return self();
    }

    protected final QR self() {
        return (QR) this;
    }

    public boolean retainAll(Collection<?> c) {
        return results.retainAll(c);
    }

    public List<T> asList() {
        return results;
    }

    public <K> QueryResults<K, QueryResults<K, ?>> map(Function<T, K> mapper) {
        List<K> mapped = new ArrayList<>();
        for (T elem : results) {
            mapped.add(mapper.apply(elem));
        }
        return new QueryResults<K, QueryResults<K, ?>>(mapped) {};
    }

    public T get(int index) {
        return results.get(index);
    }

    public T[] toArray(Object[] dest) {
        return (T[]) results.toArray(dest);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int lastIndexOf(T o) {
        return results.lastIndexOf(o);
    }

    public boolean addAll(Collection<? extends T> c) {
        return results.addAll(c);
    }

    public final QR limit(int startIndex, int amount) {
        List<T> limit = new ArrayList<>(amount);

        for (int i = startIndex; i < size() && i - startIndex < amount; i++) {
            limit.add(get(i));
        }

        results.retainAll(limit);
        return self();
    }

    public final T first() {
        return size() == 0 ? null : get(0);
    }

    public void clear() {
        results.clear();
    }

    public int size() {
        return results.size();
    }

    public T[] toArray() {
        return (T[]) results.toArray();
    }

    public String toString() {
        return getClass().getSimpleName() + results;
    }

    public boolean removeAll(Collection<?> c) {
        return results.removeAll(c);
    }

    public boolean remove(Object o) {
        return results.remove(o);
    }

    public boolean add(T t) {
        return results.add(t);
    }

    public final QR reverse() {
        Collections.reverse(results);
        return self();
    }

    public final T last() {
        int index = size();
        return index != 0 ? get(index - 1) : null;
    }

    public int indexOf(T o) {
        return results.indexOf(o);
    }

    public final T random() {
        int index = size();
        return index != 0 ? get(Random.nextInt(index)) : null;
    }

    public boolean contains(Object o) {
        return results.contains(o);
    }

    public final QR shuffle() {
        Collections.shuffle(results);
        return self();
    }

    public boolean containsAll(Collection<?> c) {
        return results.containsAll(c);
    }

    public Iterator<T> iterator() {
        return results.iterator();
    }

    public final QR limit(int entries) {
        return limit(0, entries);
    }
}
