package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.StringCommons;

public interface RSPlayer extends RSPathingEntity {
    int getCombatLevel();

    int getPrayerIcon();

    int getSkullIcon();

    int getTeam();

    int getTotalLevel();

    RSModel getTransformedNpcModel();

    RSPlayerAppearance getAppearance();

    RSNamePair getNamePair();

    boolean isHidden();

    String[] getNameTags();

    Player getWrapper();

    default String getName() {
        return StringCommons.replaceJagspace(Functions.mapOrDefault(this::getNamePair, RSNamePair::getRaw, ""));
    }
}