package org.rspeer.runetek.api.scene;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.movement.position.Position;

/**
 * Created by Yasper on 11/08/18.
 */
public final class House {

    private House() {
        throw new IllegalAccessError();
    }

    public static boolean isInside() {
        return Scene.isDynamic() && SceneObjects.getNearest(o -> o.containsAction("Lock")) != null;
    }

    public static boolean isInBuildingMode() {
        return Varps.getBoolean(780);
    }

    public static SceneObject[] getPortals() {
        if (!isInside()) {
            return new SceneObject[0];
        }
        return SceneObjects.getLoaded(e -> e.getName().contains("Portal"));
    }

    //Bits of varp 738 contain house location
    public enum Location {

        RIMMINGTON(new Position(2954, 3224)),
        TAVERLEY(new Position(2894, 3465)),
        POLLNIVNEACH(new Position(3340, 3004)),
        KOUREND(new Position(1744, 3517)),
        RELLEKKA(new Position(2670, 3632)),
        BRIMHAVEN(new Position(2704, 3128)),
        YANILLE(new Position(2544, 3095));

        private final Position outside;

        Location(Position outside) {
            this.outside = outside;
        }

        public Position getOutsidePosition() {
            return outside;
        }
    }
}
