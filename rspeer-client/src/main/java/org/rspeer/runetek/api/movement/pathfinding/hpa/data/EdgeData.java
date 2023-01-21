package org.rspeer.runetek.api.movement.pathfinding.hpa.data;

import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class EdgeData {

    private HpaLocation start, end;
    private double cost = 0;
    private double costPenalty = 0;
    private List<EdgeInteraction> interactions = new ArrayList<>();

    public HpaLocation getStart() {
        return start;
    }

    public HpaLocation getEnd() {
        return end;
    }

    public double getCost() {
        return cost;
    }

    public double getCostPenalty() {
        return costPenalty;
    }

    public List<EdgeInteraction> getInteractions() {
        return interactions;
    }
}
