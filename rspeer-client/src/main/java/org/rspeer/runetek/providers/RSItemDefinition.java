package org.rspeer.runetek.providers;

public interface RSItemDefinition extends RSDefinition {

    RSUnlitModel getEquipmentModel(boolean female);

    RSModel getModel(int i);

    int getAmbient();

    int getContrast();

    int getFemaleHeadModel();

    int getFemaleHeadModel2();

    int getFemaleModel1();

    int getFemaleModel2();

    int getId();

    int getMaleHeadModel();

    int getMaleHeadModel2();

    int getMaleModel1();

    int getMaleModel2();

    int getModelId();

    int getNoteId();

    int getNoteTemplateId();

    int getResizeX();

    int getResizeY();

    int getResizeZ();

    int getShiftClickActionIndex();

    int getSpritePitch();

    int getSpriteRoll();

    int getSpriteScale();

    int getSpriteTranslateX();

    int getSpriteTranslateY();

    int getSpriteYaw();

    int getStackable();

    int getTeam();

    int getValue();

    RSRS3CopiedNodeTable<? extends RSNode> getProperties();

    String getName();

    boolean isMembers();

    boolean isTradable();

    String[] getActions();

    String[] getGroundActions();

    int[] getVariantIds();

    int[] getVariantStackSizes();

    default boolean isStackable() {
        return getStackable() > 0;
    }

    default boolean isNoted() {
        return getNoteTemplateId() != -1;
    }

    default int getNotedId() {
        return isNoted() ? getId() : getNoteId();
    }

    default int getUnnotedId() {
        return isNoted() ? getNoteId() : getId();
    }
}