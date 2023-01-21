package org.rspeer.runetek.api.movement.pathfinding.hpa.graph;

import org.rspeer.runetek.api.movement.pathfinding.graph.Edge;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeData;

@Deprecated
public class HpaEdge implements Edge {

    private int type;
    private HpaNode start, end;
    private String pathKey;
    private double cost;
    private EdgeData customEdgeData;

    @Override
    public int getType() {
        return type;
    }

    public String getPathKey() {
        return pathKey;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public double getCostPenalty() {
        return customEdgeData != null ? customEdgeData.getCostPenalty() : 0;
    }

    public EdgeData getEdgeData() {
        return customEdgeData;
    }

    @Override
    public HpaNode getStart() {
        return start;
    }

    @Override
    public HpaNode getEnd() {
        return end;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HpaEdge{");
        sb.append("type=").append(type);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", pathKey='").append(pathKey).append('\'');
        sb.append(", cost=").append(cost);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HpaEdge)) {
            return false;
        }

        HpaEdge hpaEdge = (HpaEdge) o;

        if (getType() != hpaEdge.getType()) {
            return false;
        }
        if (getStart() != null ? !getStart().equals(hpaEdge.getStart()) : hpaEdge.getStart() != null) {
            return false;
        }
        return getEnd() != null ? getEnd().equals(hpaEdge.getEnd()) : hpaEdge.getEnd() == null;
    }

    @Override
    public int hashCode() {
        int result = getType();
        result = 31 * result + (getStart() != null ? getStart().hashCode() : 0);
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        return result;
    }
}
