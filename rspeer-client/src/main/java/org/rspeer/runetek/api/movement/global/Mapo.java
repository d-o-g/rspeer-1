package org.rspeer.runetek.api.movement.global;

import org.rspeer.runetek.api.commons.Digraph;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.global.augmenting.DirectTeleportNode;
import org.rspeer.runetek.api.movement.global.augmenting.DirectTeleportProvider;
import org.rspeer.runetek.api.movement.global.augmenting.TeleportProvider;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;

//TODO most of this is temp code just to test
public final class Mapo {

    private final Digraph<PathNode, PathNode> graph;
    private final PathFinder pathfinder;

    public Mapo() {
        graph = new Digraph<>();
        pathfinder = new AStarPathFinder(this);

        TeleportProvider data = new DirectTeleportProvider();
        data.to(this);
    }

    public void execute(PathNode node) {
        Position current = Players.getLocal().getPosition();
        Position target = node.getPosition();
        Position destination = Movement.getDestination();

        if (current.distance(target) < 5) {
            return;
        }

        if (node instanceof DirectTeleportNode) {
            if (Magic.cast(((DirectTeleportNode) node).getSpell())) {
                Time.sleepUntil(() -> current.distance() > 10, 3600);
                return;
            }
        }

        if (Players.getLocal().isMoving() && destination != null && destination.distance() < 6) {
            Movement.walkTo(node.getPosition());
        }
    }

    public PathNode getNearestTo(Position src) {
        PathNode nearest = null;
        double lowest = Integer.MAX_VALUE;
        for (PathNode node : graph) {
            double dist = node.getPosition().distance(src);
            if (dist < lowest) {
                lowest = dist;
                nearest = node;
            }
        }
        return nearest;
    }

    public PathFinder getPathfinder() {
        return pathfinder;
    }

    public Digraph<PathNode, PathNode> getGraph() {
        return graph;
    }

    public enum TraversalOption {

        WILDERNESS,
        TELEPORTATION;

        private boolean active = true;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
