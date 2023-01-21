package org.rspeer.runetek.api.movement.global;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class AStarPathFinder implements PathFinder {

    private final Mapo map;

    private final Queue<PathNode> open;
    private final List<PathNode> closed;

    public AStarPathFinder(Mapo map) {
        this.map = map;

        open = new LinkedList<>();
        closed = new LinkedList<>();
    }

    @Override
    public List<PathNode> findPath(Heuristic heuristic, PathNode src, PathNode dest) {
        src.setCost(0);

        closed.clear();
        open.clear();
        open.add(src);

        dest.setParent(null);

        while (!open.isEmpty()) {
            PathNode current = open.poll();
            if (current == dest) {
                break;
            }

            closed.add(current);
            for (PathNode edge : map.getGraph().getEdgesOf(current)) {

                if (!edge.validate()) {
                    continue;
                }

                float stepCost = current.getCost() + heuristic.getCost(current, edge);
                if (stepCost < edge.getCost()) {
                    open.remove(edge);
                    closed.remove(edge);
                }

                if (open.contains(edge) || closed.contains(edge)) {
                    continue;
                }

                edge.setCost(stepCost);
                edge.setHeuristic(heuristic.getCost(edge, dest));
                edge.setParent(current);
                open.add(edge);
            }
        }

        if (src.equals(dest)) {
            dest.setParent(src);
        }

        List<PathNode> path = new LinkedList<>();
        if (dest.getParent() == null) {
            return path;
        }

        PathNode target = dest;
        while (target != src) {
            path.add(target);
            target = target.getParent();
        }
        path.add(src);
        gc();
        return path;
    }

    private void gc() {
        closed.clear();
        open.clear();
        System.gc();
    }
}
