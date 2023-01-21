package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.providers.annotations.Synthetic;

public interface RSObjectDefinition extends RSDefinition {

    int getMapDoorFlag();

    int getAmbient();

    int getAmbientSoundId();

    int getAnimation();

    int getClipType();

    int getContrast();

    int getId();

    int getItemSupport();

    int getMapFunction();

    int getMapSceneId();

    int getScaleX();

    int getScaleY();

    int getScaleZ();

    int getSizeX();

    int getSizeY();

    int getTranslateX();

    int getTranslateY();

    int getTranslateZ();

    int getVarpIndex();

    int getVarpbitIndex();

    RSRS3CopiedNodeTable getProperties();

    String getName();

    boolean isClipped();

    boolean isImpenetrable();

    boolean isProjectileClipped();

    boolean isRotated();

    boolean isSolid();

    int[] getTransformIds();

    int[] getModelIds();

    String[] getActions();

    short[] getColors();

    short[] getNewColors();

    short[] getNewTextures();

    short[] getTextures();

    @Synthetic
    default RSObjectDefinition transform() {
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

        return transformedId != -1 ? Definitions.getObject(transformedId) : null;
    }
}