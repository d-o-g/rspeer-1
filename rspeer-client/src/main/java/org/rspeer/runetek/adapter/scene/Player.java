package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.providers.RSModel;
import org.rspeer.runetek.providers.RSNamePair;
import org.rspeer.runetek.providers.RSPlayer;
import org.rspeer.runetek.providers.RSPlayerAppearance;

import java.util.ArrayList;
import java.util.List;

public final class Player extends PathingEntity<RSPlayer, Player> implements RSPlayer {

    private int index = -1;

    public Player(RSPlayer provider) {
        super(provider);
    }

    @Override
    public int getId() {
        return provider.getIndex();
    }

    @Override
    public int getIndex() {
        if (index != -1) {
            return index;
        }
        RSPlayer[] players = Game.getClient().getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (provider == players[i]) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public String getName() {
        return provider.getName();
    }

    @Override
    public boolean isHidden() {
        return provider.isHidden();
    }

    @Override
    public String[] getNameTags() {
        String[] arr = provider.getNameTags();
        if (arr == null) {
            return new String[0];
        }

        List<String> tags = new ArrayList<>();
        for (String tag : arr) {
            if (tag != null) {
                tags.add(tag);
            }
        }
        return tags.toArray(new String[0]);
    }

    @Override
    public String[] getActions() {
        String[] arr = Game.getClient().getPlayerActions();
        if (arr == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : arr) {
            if (action != null) {
                actions.add(StringCommons.replaceColorTag(action));
            }
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public String[] getRawActions() {
        return Game.getClient().getPlayerActions();
    }

    @Override
    public Player getWrapper() {
        return provider.getWrapper();
    }

    @Override
    public int getCombatLevel() {
        return provider.getCombatLevel();
    }

    @Override
    public int getPrayerIcon() {
        return provider.getPrayerIcon();
    }

    @Override
    public int getSkullIcon() {
        return provider.getSkullIcon();
    }

    @Override
    public int getTeam() {
        return provider.getTeam();
    }

    @Override
    public int getTotalLevel() {
        return provider.getTotalLevel();
    }

    @Override
    public RSModel getTransformedNpcModel() {
        return provider.getTransformedNpcModel();
    }

    @Override
    public RSPlayerAppearance getAppearance() {
        return provider.getAppearance();
    }

    @Override
    public RSNamePair getNamePair() {
        return provider.getNamePair();
    }
}
