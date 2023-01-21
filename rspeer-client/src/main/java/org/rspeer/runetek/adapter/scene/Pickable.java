package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.Onymous;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.rspeer.runetek.providers.RSPickable;

import java.util.ArrayList;
import java.util.List;

public final class Pickable extends Entity<RSPickable, Pickable>
        implements RSPickable, Interactable, Identifiable, Onymous {

    private final int sceneX, sceneY, floorLevel;
    private RSItemDefinition definition;

    public Pickable(RSPickable provider, int sceneX, int sceneY, int floorLevel) {
        super(provider);
        this.sceneX = sceneX;
        this.sceneY = sceneY;
        this.floorLevel = floorLevel;
    }

    @Override
    public int getSceneX() {
        return sceneX;
    }

    @Override
    public int getSceneY() {
        return sceneY;
    }

    @Override
    public int getFloorLevel() {
        return floorLevel;
    }

    @Override
    public int getId() {
        return provider.getId();
    }

    @Override
    public int getStackSize() {
        return provider.getStackSize();
    }

    public Pickable getWrapper() {
        return this;
    }

    public RSItemDefinition getDefinition() {
        if (definition == null) {
            definition = Definitions.getItem(provider.getId());
        }
        return definition;
    }

    @Override
    public String[] getActions() {
        RSItemDefinition definition = getDefinition();
        if (definition == null || definition.getGroundActions() == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : definition.getGroundActions()) {
            if (action != null) {
                actions.add(StringCommons.replaceColorTag(action));
            }
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public String getName() {
        return Functions.mapOrDefault(this::getDefinition, RSItemDefinition::getName, "");
    }

    @Override
    public String[] getRawActions() {
        RSItemDefinition definition = getDefinition();
        return definition == null ? new String[0] : definition.getGroundActions();
    }

    public boolean isStackable() {
        return Functions.mapOrElse(this::getDefinition, RSItemDefinition::isStackable);
    }

    public boolean isNoted() {
        return Functions.mapOrElse(this::getDefinition, RSItemDefinition::isNoted);
    }
}
