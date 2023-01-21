package org.rspeer.runetek.api.movement.global;

import org.rspeer.runetek.api.commons.math.DistanceEvaluator;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Condition;
import org.rspeer.script.task.Executable;

public class PathNode implements Comparable<PathNode>, Condition {

    private final Type type;
    private final Position position;

    private float cost;
    private float heuristic;

    private PathNode parent;

    public PathNode(Type type, Position position) {
        this.type = type;
        this.position = position;
    }

    public Type getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(float heuristic) {
        this.heuristic = heuristic;
    }

    public PathNode getParent() {
        return parent;
    }

    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getFloorLevel() {
        return position.getFloorLevel();
    }

    public double distance(PathNode other, DistanceEvaluator evaluator) {
        return evaluator.evaluate(position, other.position);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public int compareTo(PathNode o) {
        return Float.compare(heuristic + cost, o.heuristic + o.cost);
    }

    public enum Type {
        STANDARD,
        OBJECT,
        NPC,
        DIRECT_TELEPORT,
        FAIRY_RING
    }
}