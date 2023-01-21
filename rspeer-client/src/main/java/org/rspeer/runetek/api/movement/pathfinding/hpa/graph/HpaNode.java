package org.rspeer.runetek.api.movement.pathfinding.hpa.graph;

import org.rspeer.runetek.api.movement.pathfinding.graph.Node;
import org.rspeer.runetek.api.movement.position.Position;

@Deprecated
public class HpaNode implements Node {

    private int type;
    private HpaLocation location;

    @Override
    public Position getPosition() {
        return location.toPosition();
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "HpaNode{" +
                "type=" + type +
                ", location=" + location +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HpaNode)) {
            return false;
        }

        HpaNode hpaNode = (HpaNode) o;

        return location != null ? location.equals(hpaNode.location) : hpaNode.location == null;
    }

    @Override
    public int hashCode() {
        return location != null ? location.hashCode() : 0;
    }
}
