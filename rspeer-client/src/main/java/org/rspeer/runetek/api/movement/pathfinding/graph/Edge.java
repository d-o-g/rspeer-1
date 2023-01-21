package org.rspeer.runetek.api.movement.pathfinding.graph;

/**
 * Created by Zachary Herridge on 8/1/2018.
 */
public interface Edge {

    Node getStart();

    Node getEnd();

    int getType();

    double getCost();

    double getCostPenalty();
}
