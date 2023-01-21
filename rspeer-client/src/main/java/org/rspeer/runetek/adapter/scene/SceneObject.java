package org.rspeer.runetek.adapter.scene;

import org.rspeer.runetek.adapter.Adapter;
import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.commons.*;
import org.rspeer.runetek.api.input.menu.interaction.InteractDriver;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.providers.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class SceneObject extends Adapter<RSSceneObject, SceneObject>
        implements RSSceneObject, Interactable, Positionable, Identifiable, Rotatable, Onymous {

    private static final Dimension ONE_BY_ONE = new Dimension(1, 1);

    private RSObjectDefinition definition;

    public SceneObject(RSSceneObject provider) {
        super(provider);
    }

    public boolean isTransformed() {
        if (definition == null) {
            definition = Definitions.getObject(getId());
        }
        return definition != null && definition.transform() != null;
    }

    public RSObjectDefinition getDefinition() {
        if (definition == null) {
            definition = Definitions.getObject(getId());
        }
        if (definition != null) {
            RSObjectDefinition trans = definition.transform();
            if (trans != null) {
                return trans;
            }
        }
        return definition;
    }

    public String getName() {
        RSObjectDefinition definition = getDefinition();
        return definition == null || definition.getName() == null ? "" : definition.getName();
    }

    @Override
    public long getUid() {
        return provider.getUid();
    }

    @Override
    public RSEntity getEntity() {
        return provider.getEntity();
    }

    @Override
    public SceneObject getWrapper() {
        return provider.getWrapper();
    }

    @Override
    public int getSceneX() {
        return provider.getSceneX();
    }

    @Override
    public int getSceneY() {
        return provider.getSceneY();
    }

    public int getFloorLevel() {
        return provider.getFloorLevel();
    }

    @Override
    public int getId() {
        return provider.getId();
    }

    @Override
    public boolean interact(String action) {
        return InteractDriver.INSTANCE.interact(this, action);
    }

    public String[] getActions() {
        RSObjectDefinition definition = getDefinition();
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
        return Functions.mapOrDefault(this::getDefinition, RSObjectDefinition::getActions, new String[0]);
    }

    public short[] getColors() {
        RSObjectDefinition definition = getDefinition();
        if (definition == null) {
            return new short[0];
        }

        short[] colors = definition.getNewColors();
        return colors != null ? colors : new short[0];
    }

    @Override
    public Position getPosition() {
        return new Position(Scene.getBaseX() + getSceneX(), Scene.getBaseY() + getSceneY(), getFloorLevel());
    }

    public int getOrientation() {
        int orientation = provider.getOrientation();
        if (orientation == Integer.MIN_VALUE) {
            orientation = provider.getLinkedOrientation();
        }

        if (orientation == Integer.MIN_VALUE) {
            orientation = Functions.mapOrDefault(provider::asDynamicObject, RSDynamicObject::getOrientation, Integer.MIN_VALUE);
        }

        return orientation;
    }

    public Dimension getSize() {
        RSObjectDefinition def = getDefinition();
        return def != null ? new Dimension(def.getSizeX(), def.getSizeY()) : ONE_BY_ONE;
    }

    public Area getArea() {
        Dimension dimension = getSize();
        Position min = getPosition();
        if (dimension.equals(ONE_BY_ONE)) {
            return Area.singular(min);
        }

        if (provider instanceof RSEntityMarker) {
            RSEntityMarker m = (RSEntityMarker) provider;
            Position max = new ScenePosition(m.getMaxSceneX(), m.getMaxSceneY(), getFloorLevel()).getPosition();
            return Area.rectangular(min, max);
        }

        return Area.singular(min);
    }
}
