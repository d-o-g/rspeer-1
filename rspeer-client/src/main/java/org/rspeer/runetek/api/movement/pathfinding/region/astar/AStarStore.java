package org.rspeer.runetek.api.movement.pathfinding.region.astar;

import org.rspeer.runetek.api.movement.pathfinding.graph.Node;

public class AStarStore implements Comparable<AStarStore> {

    private Node node;
    private double priority;

    public AStarStore(Node node, double priority) {
        this.node = node;
        this.priority = priority;
    }

    public Node getNode() {
        return node;
    }

    public double getPriority() {
        return priority;
    }

    @Override
    public int compareTo(AStarStore o) {
        return Double.compare(getPriority(), o.getPriority());
    }
}