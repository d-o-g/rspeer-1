package org.rspeer.runetek.api.movement.pathfinding.region.graph;


import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.graph.Edge;

/**
 * Created by Zachary Herridge on 6/11/2018.
 */

public class TileEdge implements Edge {

    private TileNode tileStart, tileEnd;

    private double costPenalty = 0;
    private int type = EdgeType.BASIC;

    public TileEdge(TileNode tileStart, TileNode tileEnd) {
        this.tileStart = tileStart;
        this.tileEnd = tileEnd;
    }

    public TileEdge(TileNode tileStart, TileNode tileEnd, double costPenalty) {
        this.tileStart = tileStart;
        this.tileEnd = tileEnd;
        this.costPenalty = costPenalty;
    }

    public TileEdge() {
    }

    public TileNode getTileStart() {
        return tileStart;
    }

    public TileNode getTileEnd() {
        return tileEnd;
    }

    public double getCostPenalty() {
        return costPenalty;
    }

    @Override
    public int getType() {
        return type;
    }

    public TileEdge setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public double getCost() {
        return 1;
    }

    @Override
    public TileNode getStart() {
        return tileStart;
    }

    @Override
    public TileNode getEnd() {
        return tileEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TileEdge)) {
            return false;
        }

        TileEdge tileEdge = (TileEdge) o;

        if (Double.compare(tileEdge.getCostPenalty(), getCostPenalty()) != 0) {
            return false;
        }
        if (getType() != tileEdge.getType()) {
            return false;
        }
        if (getTileStart() != null ? !getTileStart().equals(tileEdge.getTileStart()) : tileEdge.getTileStart() != null) {
            return false;
        }
        return getTileEnd() != null ? getTileEnd().equals(tileEdge.getTileEnd()) : tileEdge.getTileEnd() == null;
    }

    @Override
    public int hashCode() {
        int result = getTileStart() != null ? getTileStart().hashCode() : 0;
        result = 31 * result + (getTileEnd() != null ? getTileEnd().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TileEdge{" +
                "start=" + tileStart +
                ", end=" + tileEnd +
                ", costPenalty=" + costPenalty +
                ", type=" + type +
                '}';
    }
}
