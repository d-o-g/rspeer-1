package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.GraphicsObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.NodeDeque;
import org.rspeer.runetek.api.commons.predicate.IdPredicate;
import org.rspeer.runetek.providers.RSGraphicsObject;
import org.rspeer.runetek.providers.RSNode;
import org.rspeer.runetek.providers.RSNodeDeque;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by jasper on 02/08/18.
 */
public final class GraphicsObjects {

    private GraphicsObjects() {
        throw new IllegalAccessError();
    }

    public static GraphicsObject[] getLoaded(Predicate<GraphicsObject> predicate) {
        RSNodeDeque<RSGraphicsObject> rsNodeDeq = Game.getClient().getGraphicsObjectDeque();
        NodeDeque deqWrapper = new NodeDeque(rsNodeDeq);
        List<GraphicsObject> result = new ArrayList<>();
        for (RSNode node : deqWrapper) {
            if (node instanceof RSGraphicsObject) {
                GraphicsObject obj = Functions.mapOrDefault(() -> (RSGraphicsObject) node, GraphicsObject::new, null);
                if (obj != null && predicate.test(obj)) {
                    result.add(obj);
                }
            }
        }

        return result.toArray(new GraphicsObject[0]);
    }

    public static GraphicsObject[] getLoaded(int id) {
        return getLoaded(new IdPredicate<>(id));
    }

    public static GraphicsObject getNearest(Predicate<GraphicsObject> predicate) {
        double dist = Double.MAX_VALUE;
        GraphicsObject nearest = null;
        for (GraphicsObject loaded : getLoaded(predicate)) {
            double currDist = loaded.distance();
            if (nearest == null || currDist < dist) {
                nearest = loaded;
                dist = currDist;
            }
        }

        return nearest;
    }

    public static GraphicsObject getNearest(int id) {
        return getNearest(new IdPredicate<>(id));
    }
}
