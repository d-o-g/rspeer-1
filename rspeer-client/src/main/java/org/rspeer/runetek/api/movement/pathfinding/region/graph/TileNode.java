package org.rspeer.runetek.api.movement.pathfinding.region.graph;


import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.graph.Node;
import org.rspeer.runetek.api.movement.pathfinding.region.util.CollisionFlags;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Direction;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Reachable;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Scene;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.rspeer.runetek.api.movement.pathfinding.region.util.Direction.*;


/**
 * Created by Zachary Herridge on 6/11/2018.
 */

public class TileNode implements Node {

    protected Position position;
    protected int type;

    public TileNode(Position position) {
        this.position = position;
    }

    public int getX() {
        return getPosition().getX();
    }

    public int getY() {
        return getPosition().getY();
    }

    public int getPlane() {
        return getPosition().getFloorLevel();
    }

    public Collection<TileEdge> getNeighbors() {
        return getNeighbors(false);
    }

    public Collection<TileEdge> getNeighbors(boolean ignoreSelfBlocked) {
        Set<TileEdge> edges = new HashSet<>(8);
        boolean east = addEdge(edges, EAST, ignoreSelfBlocked);
        boolean west = addEdge(edges, WEST, ignoreSelfBlocked);
        if (addEdge(edges, NORTH, ignoreSelfBlocked)) {
            if (east) {
                addEdge(edges, NORTH_EAST, ignoreSelfBlocked);
            }
            if (west) {
                addEdge(edges, NORTH_WEST, ignoreSelfBlocked);
            }
        }

        if (addEdge(edges, SOUTH, ignoreSelfBlocked)) {
            if (east) {
                addEdge(edges, SOUTH_EAST, ignoreSelfBlocked);
            }
            if (west) {
                addEdge(edges, SOUTH_WEST, ignoreSelfBlocked);
            }
        }
        return edges;
    }

    public Position getPosition() {
        return position;
    }

    protected boolean addEdge(Set<TileEdge> edges, Direction direction, boolean ignoreStartBlocked) {
        Position start = getPosition();
        Position end = start.translate(direction.getXOff(), direction.getYOff());

        TileNode endNode = new TileNode(end);
        if (walkable(start, direction, ignoreStartBlocked)) {
            edges.add(new TileEdge(this, endNode));
            return true;
        } else {
            boolean endDoor = Reachable.containsDoor(direction, end);
            boolean startDoor = Reachable.containsDoor(direction, start);
            if (startDoor) {
                setType(EdgeType.DOOR);
            }
            if (endDoor) {
                endNode.setType(EdgeType.DOOR);
            }

            if ((endDoor || startDoor) && (endDoor || !Scene.isBlocked(end))
                    && (!Scene.isBlocked(start) || walkable(start, direction, true))) {
                TileEdge tileEdge = new TileEdge(this, endNode, 4);
                tileEdge.setType(EdgeType.DOOR);
                edges.add(tileEdge);
                //Possibly return false when contains door to replicate previous performance.
                return true;
            }
        }
        return false;
    }

    private boolean walkable(Position startLocation, Direction direction, boolean ignoreStartBlocked) {
        try {
            Position endLocation = startLocation.translate(direction.getXOff(), direction.getYOff());
            int startFlag = Scene.getCollisionFlag(startLocation);
            int endFlag = Scene.getCollisionFlag(endLocation);
            return CollisionFlags.checkWalkable(direction, startFlag, endFlag, ignoreStartBlocked);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TileNode)) {
            return false;
        }
        TileNode tileNode = (TileNode) object;
        return getPosition() != null ? getPosition().equals(tileNode.getPosition()) : tileNode.getPosition() == null;
    }

    @Override
    public int hashCode() {
        return getPosition() != null ? getPosition().hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TileNode{");
        sb.append("location=").append(position);
        sb.append('}');
        return sb.toString();
    }
}
