package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.providers.annotations.Synthetic;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;

public interface RSNpcDefinition extends RSDefinition {
    int getId();

    int getIdleAnimation();

    int getPrayerIcon();

    int getScaleXY();

    int getScaleZ();

    int getSize();

    int getVarpIndex();

    int getVarpbitIndex();

    int getWalkAnimation();

    RSRS3CopiedNodeTable getProperties();

    String getName();

    boolean isRenderedOnMinimap();

    boolean isRenderingPrioritized();

    int[] getTransformIds();

    String[] getActions();

    short[] getColors();

    short[] getNewColors();

    short[] getNewTextures();

    short[] getTextures();

    int getCombatLevel();

    int[] getModelIds();

    @Synthetic
    default RSNpcDefinition transform() {
        int[] transformIds = getTransformIds();

        if (transformIds == null) {
            return null;
        }

        int varpbitIndex = getVarpbitIndex();
        int varpIndex = getVarpIndex();
        int transformIndex = -1;
        int transformedId;

        if (varpbitIndex != -1) {
            transformIndex = Varps.getBitValue(varpbitIndex);
        } else if (varpIndex != -1) {
            transformIndex = Varps.get(varpIndex);
        }

        if (transformIndex >= 0 && transformIndex < transformIds.length - 1) {
            transformedId = transformIds[transformIndex];
        } else {
            transformedId = transformIds[transformIds.length - 1];
        }

        return transformedId != -1 ? Definitions.getNpc(transformedId) : null;
    }
}