package org.rspeer.runetek.providers;

public interface RSPlayerAppearance extends RSProvider {

    RSModel getModel(RSAnimationSequence as, int i, RSAnimationSequence as2, int i2);

    int getTransformedNpcId();

    void setTransformedNpcId(int id);

    boolean isFemale();

    int[] getEquipmentIds();

    int[] getIds();

    default boolean isEquipped(int... ids) {
        for (int id : getEquipmentIds()) {
            for (int check : ids) {
                if (check == id - 512) {
                    return true;
                }
            }
        }
        return false;
    }
}