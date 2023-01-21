package org.rspeer.runetek.api.commons;

public final class OverheadIcons {

    private OverheadIcons() {
        throw new IllegalAccessError();
    }

    public interface Skull {
        int STANDARD = 0;
        int FIGHT_PITS_RED = 1;
        int RED = 2;
        int BLUE = 3;
        int GREEN = 4;
        int SILVER = 5;
        //int IDK_GREENSILVERRED = 6;
        int BRONZE = 7;
        int RED_KEYS = 8;
        int BLUE_KEYS = 9;
        int GREEN_KEYS = 10;
        int SILVER_KEYS = 11;
        int BRONZE_KEYS = 12;
    }

    public interface Prayer {
        int PROTECT_FROM_MELEE = 0;
        int PROTECT_FROM_MISSILES = 1;
        int PROTECT_FROM_MAGIC = 2;
        int RETRIBUTION = 3;
        int SMITE = 4;
        int REDEMPTION = 5;
        int PROTECT_FROM_MISSILES_AND_MAGIC = 6;
    }
}
