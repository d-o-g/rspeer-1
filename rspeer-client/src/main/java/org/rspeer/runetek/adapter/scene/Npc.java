package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.providers.RSNpc;
import org.rspeer.runetek.providers.RSNpcDefinition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Npc extends PathingEntity<RSNpc, Npc> implements RSNpc, Interactable {

    private static final Dimension ONE_BY_ONE = new Dimension(1, 1);

    public Npc(RSNpc provider) {
        super(provider);
    }

    @Override
    public int getId() {
        return Functions.mapOrDefault(this::getDefinition, RSNpcDefinition::getId, -1);
    }

    @Override
    public String getName() {
        return Functions.mapOrDefault(this::getDefinition, RSNpcDefinition::getName, "");
    }

    @Override
    public int getCombatLevel() {
        return Functions.mapOrDefault(this::getDefinition, RSNpcDefinition::getCombatLevel, -1);
    }

    @Override
    public RSNpcDefinition getDefinition() {
        RSNpcDefinition def = provider.getDefinition();
        if (def != null) {
            RSNpcDefinition trans = def.transform();
            if (trans != null) {
                return trans;
            }
        }
        return def;
    }

    @Override
    public Npc getWrapper() {
        return provider.getWrapper();
    }

    @Override
    public String[] getActions() {
        RSNpcDefinition definition = getDefinition();
        if (definition == null || definition.getActions() == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : definition.getActions()) {
            if (action != null) {
                actions.add(StringCommons.replaceColorTag(action));
            }
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public String[] getRawActions() {
        return Functions.mapOrDefault(this::getDefinition, RSNpcDefinition::getActions, new String[0]);
    }

    public Dimension getSize() {
        RSNpcDefinition def = getDefinition();
        return def != null ? new Dimension(def.getSize(), def.getSize()) : ONE_BY_ONE;
    }

    public Area getArea() {
        Dimension dimension = getSize();
        Position min = getPosition();
        if (dimension.equals(ONE_BY_ONE)) {
            return Area.singular(min);
        }
        return Area.rectangular(min, min.translate(dimension.width, dimension.height));
    }

    @Override
    public String toString() {
        return "Npc[" + getName() + " | " + getCombatLevel() + " | " + getId() + "]";
    }
}
