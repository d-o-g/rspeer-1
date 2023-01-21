package org.rspeer.runetek.api.commons;

import java.util.*;

public class Multiset<E> extends LinkedList<E> {

    private final Map<E, Integer> counts = new HashMap<>();

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        if (!counts.containsKey(element)) {
            counts.put(element, 0);
        }
        counts.put(element, counts.get(element) + 1);
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        for (E element : elements) {
            if (!add(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        counts.remove(o);
        return removed;
    }

    @Override
    public void clear() {
        super.clear();
        counts.clear();
    }

    public int getCount(E element) {
        if (!counts.containsKey(element)) {
            return 0;
        }
        return counts.get(element);
    }

    public int getUniqueCount() {
        return counts.size();
    }

    public E getHighest() {
        E element = null;
        int count = 0;
        for (Map.Entry<E, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > count) {
                element = entry.getKey();
                count = entry.getValue();
            }
        }
        return element;
    }

    public E getLowest() {
        E element = null;
        int count = Integer.MAX_VALUE;
        for (Map.Entry<E, Integer> entry : counts.entrySet()) {
            if (entry.getValue() < count) {
                element = entry.getKey();
                count = entry.getValue();
            }
        }
        return element;
    }
}