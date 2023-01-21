package org.rspeer.runetek.api.movement.debug;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.BresenhamPath;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.EdgeType;
import org.rspeer.runetek.api.movement.pathfinding.graph.Edge;
import org.rspeer.runetek.api.movement.pathfinding.graph.Node;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;
import org.rspeer.runetek.api.movement.pathfinding.region.astar.AStar;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileEdge;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TileNode;
import org.rspeer.runetek.api.movement.pathfinding.region.graph.TilePath;
import org.rspeer.runetek.api.movement.pathfinding.region.util.Reachable;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Zachary Herridge on 7/9/2018.
 */
public class MovementDebug implements RenderListener {

    private Path lastBuiltPath;

    private Reachable lastReachable;

    private boolean toggle = false;
    private AStar lastAStar;

    public void toggle() {
        if (isToggled()) {
            Game.getEventDispatcher().deregister(this);
        } else {
            Game.getEventDispatcher().register(this);
        }
        toggle = !isToggled();
    }

    public boolean isToggled() {
        return toggle;
    }

    public void clear() {
        lastBuiltPath = null;
        lastReachable = null;
        lastAStar = null;
    }

    @Override
    public void notify(RenderEvent event) {
        if (!Game.isLoggedIn()) {
            return;
        }
        Color before = event.getSource().getColor();

        BankLocation nearest = BankLocation.getNearest();
        if (nearest != null) {
            outlineTile(nearest.getPosition(), event.getSource(), Color.ORANGE);
        }

        if (lastAStar != null) {
            for (Node tileNode : lastAStar.getCostCache().keySet()) {
                markMiniMap(tileNode.getPosition(), event.getSource(), Color.ORANGE);
            }
        }

        if (lastReachable != null) {
            for (Map.Entry<Position, TileEdge> entry : lastReachable.getCameFrom().entrySet()) {
                markMiniMap(
                        Projection.toMinimap(entry.getValue().getTileStart().getPosition(), true),
                        event.getSource(),
                        lastReachable.isActionRequired(entry.getKey()) ? Color.BLUE : Color.ORANGE
                );
            }
        }

        if (lastBuiltPath != null) {
            if (lastBuiltPath instanceof HpaPath) {
                drawHpaPath((HpaPath) lastBuiltPath, event.getSource());
            } else if (lastBuiltPath instanceof BresenhamPath) {
                drawBresenhamPath((BresenhamPath) lastBuiltPath, event.getSource());
            } else if (lastBuiltPath instanceof TilePath) {
                drawTilePath((TilePath) lastBuiltPath, event.getSource(), true);
            }
        }

        Position destination = Movement.getDestination();
        if (destination != null) {
            markMiniMap(destination, event.getSource(), Color.GREEN);
        }

        for (TileEdge tileEdge : new TileNode(Players.getLocal().getPosition()).getNeighbors()) {
            outlineTile(tileEdge.getTileEnd().getPosition(), event.getSource(), EdgeType.DEBUG_COLORS[tileEdge.getTileEnd().getType()]);
        }

        event.getSource().setColor(before);
    }

    public void drawBresenhamPath(BresenhamPath bresenhamPath, Graphics graphics) {
        if (bresenhamPath == null) {
            return;
        }

        Position last = null;
        for (Position current : bresenhamPath.getLine()) {
            if (last != null) {
                markAndConnect(current, last, graphics, Color.PINK);
            }
            last = current;
        }

        if (bresenhamPath.getLastTilePath() != null) {
            drawTilePath(bresenhamPath.getLastTilePath(), graphics, false);
        }
    }

    public void drawTilePath(TilePath tilePath, Graphics graphics, boolean miniMap) {
        if (tilePath == null) {
            return;
        }

        for (Edge tileEdge : tilePath.getPath()) {
            if (miniMap) {
                markAndConnect(tileEdge.getStart().getPosition(), tileEdge.getEnd().getPosition(), graphics, Color.WHITE);
            }
            outlineTile(tileEdge.getStart().getPosition(), graphics, EdgeType.DEBUG_COLORS[tileEdge.getType()]);
            outlineTile(tileEdge.getEnd().getPosition(), graphics, EdgeType.DEBUG_COLORS[tileEdge.getType()]);
        }
    }

    public void drawHpaPath(HpaPath hpaPath, Graphics graphics) {
        if (hpaPath == null) {
            return;
        }

        drawTilePath(hpaPath.getLastTilePath(), graphics, false);

        for (HpaEdge hpaEdge : hpaPath.getPath()) {
            if (Movement.isWalkable(hpaEdge.getEnd().getPosition())) {
                outlineTile(hpaEdge.getEnd().getPosition(), graphics, hpaEdge.getEnd().getType() == 4 ? Color.MAGENTA : Color.BLUE);
            }
        }

        if (hpaPath.getSubPaths() != null) {
            for (List<HpaLocation> subPath : hpaPath.getSubPaths().values()) {
                HpaLocation lastPosition = null;
                for (HpaLocation hpaLocation : subPath) {
                    if (Movement.isWalkable(hpaLocation.toPosition(), false)) {
                        Color color = Color.BLUE;
                        if (lastPosition != null) {
                            markAndConnect(lastPosition.toPosition(), hpaLocation.toPosition(), graphics, color);
                        }
                        lastPosition = hpaLocation;
                    }
                }
            }
        }

        if (hpaPath.getPath() != null) {
            for (HpaEdge hpaEdge : hpaPath.getPath()) {
                if (hpaEdge.getStart() == null || hpaEdge.getEnd() == null) {
                    continue;
                }

                Color color = Color.MAGENTA;
                if (!Movement.isWalkable(hpaEdge.getEnd().getPosition(), false)) {
                    color = Color.GRAY;
                }
                markAndConnect(hpaEdge.getStart().getPosition(), hpaEdge.getEnd().getPosition(), graphics, color);
            }
        }
    }

    public void markAndConnect(Position start, Position end, Graphics graphics, Color color) {
        Point sPoint = Projection.toMinimap(start, true);
        Point ePoint = Projection.toMinimap(end, true);

        markMiniMap(sPoint, graphics, color);
        markMiniMap(ePoint, graphics, color);

        if (ePoint != null && sPoint != null) {
            graphics.setColor(color);
            graphics.drawLine(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
        }
    }

    public void markMiniMap(Position position, Graphics graphics, Color color) {
        markMiniMap(Projection.toMinimap(position, true), graphics, color);
    }

    public void markMiniMap(Point point, Graphics graphics, Color color) {
        if (point != null) {
            graphics.setColor(color);
            graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
        }
    }

    public void outlineTile(Position position, Graphics graphics, Color color) {
        graphics.setColor(color);
        Polygon tileShape = Projection.getTileShape(position);
        if (tileShape != null) {
            graphics.drawPolygon(tileShape);
        }
    }

    public void setLastReachable(Reachable lastReachable) {
        this.lastReachable = lastReachable;
    }

    public void setLastBuiltPath(Path lastBuiltPath) {
        this.lastBuiltPath = lastBuiltPath;
    }

    public void setLastAStar(AStar lastAStar) {
        this.lastAStar = lastAStar;
    }
}
