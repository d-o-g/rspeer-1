package org.rspeer.runetek.providers;

import java.util.ArrayList;
import java.util.List;

public interface RSTile extends RSNode {
    int getFloorLevel();

    int getSceneX();

    int getSceneY();

    RSTileModel getModel();

    RSPickablePile getPickablePile();

    RSTileDecor getDecor();

    RSBoundaryDecor getBoundaryDecor();

    RSTilePaint getPaint();

    RSBoundary getBoundary();

    RSEntityMarker[] getMarkers();

    default RSSceneObject[] getObjects() {
        List<RSSceneObject> components = new ArrayList<>();
        for (RSEntityMarker entity : getMarkers()) {
            if (entity != null && entity.getType() == 2) {
                components.add(entity);
            }
        }

        RSBoundary boundary = getBoundary();
        RSBoundaryDecor boundaryDecor = getBoundaryDecor();
        RSTileDecor tileDecor = getDecor();

        if (boundary != null) {
            components.add(boundary);
        }

        if (boundaryDecor != null) {
            components.add(boundaryDecor);
        }

        if (tileDecor != null) {
            components.add(tileDecor);
        }

        return components.toArray(new RSSceneObject[0]);
    }
}