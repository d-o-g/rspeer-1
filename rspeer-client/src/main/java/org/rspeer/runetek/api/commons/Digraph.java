package org.rspeer.runetek.api.commons;

import java.util.*;

public class Digraph<V, E> implements Iterable<V> {

    private final Map<V, Set<E>> graph = new HashMap<>();

    public final int size() {
        return graph.size();
    }

    public final boolean containsVertex(V vertex) {
        return graph.containsKey(vertex);
    }

    public final boolean containsEdge(V vertex, E edge) {
        return graph.containsKey(vertex) && graph.get(vertex).contains(edge);
    }

    public final boolean addVertex(V vertex) {
        if (!graph.containsKey(vertex)) {
            graph.put(vertex, new HashSet<E>());
            return true;
        }
        return false;
    }

    public final void addEdge(V start, E dest) {
        if (graph.containsKey(start)) {
            graph.get(start).add(dest);
        }
    }

    public final void removeEdge(V start, E dest) {
        if (graph.containsKey(start)) {
            graph.get(start).remove(dest);
        }
    }

    public final Set<E> getEdgesOf(V node) {
        return Collections.unmodifiableSet(graph.get(node));
    }

    public final void merge(Digraph<V, E> graph) {
        this.graph.putAll(graph.graph);
    }

    public final void clear() {
        graph.clear();
    }

    public final List<V> getVertices() {
        return new ArrayList<>(graph.keySet());
    }

    public final boolean isEmpty() {
        return graph.size() == 0;
    }

    @Override
    public final Iterator<V> iterator() {
        return graph.keySet().iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (V v : graph.keySet()) {
            builder.append("\n    ").append(v).append(" -> ").append(graph.get(v));
        }
        return builder.toString();
    }
}