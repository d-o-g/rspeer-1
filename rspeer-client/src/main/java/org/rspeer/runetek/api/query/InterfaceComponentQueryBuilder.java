package org.rspeer.runetek.api.query;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.query.results.InterfaceComponentQueryResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class InterfaceComponentQueryBuilder
        extends QueryBuilder<InterfaceComponent, InterfaceComponentQueryBuilder, InterfaceComponentQueryResults> {

    private final Supplier<List<? extends InterfaceComponent>> provider;


    private Predicate<String> text = null;
    private Predicate<String> action = null;
    private Predicate<String> name = null;

    private Boolean visible = null;

    private int[] materials = null;
    private int[] types = null;
    private int[] contentTypes = null;
    private int[] foregrounds = null;
    private int[] modelIds = null;

    private boolean includeSubcomponents = false;

    public InterfaceComponentQueryBuilder(Supplier<List<? extends InterfaceComponent>> provider) {
        this.provider = provider;
    }

    public InterfaceComponentQueryBuilder() {
        provider = () -> {
            List<InterfaceComponent> components = new ArrayList<>();
            for (InterfaceComponent[] group : Interfaces.getAll()) {
                for (InterfaceComponent component : group) {
                    components.add(component);
                    if (includeSubcomponents) {
                        Collections.addAll(components, component.getComponents());
                    }
                }
            }
            return components;
        };
    }

    @Override
    public Supplier<List<? extends InterfaceComponent>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected InterfaceComponentQueryResults createQueryResults(Collection<? extends InterfaceComponent> raw) {
        return new InterfaceComponentQueryResults(raw);
    }

    public InterfaceComponentQueryBuilder groups(int... groups) {
        return provider(() -> {
            List<InterfaceComponent> components = new ArrayList<>();
            for (int group : groups) {
                for (InterfaceComponent component : Interfaces.get(group)) {
                    components.add(component);
                    if (includeSubcomponents) {
                        Collections.addAll(components, component.getComponents());
                    }
                }
            }
            return components;
        });
    }

    /**
     * @deprecated lol don't use hardcoded component indices
     */
    public InterfaceComponentQueryBuilder components(int group, int... componentIndices) {
        includeSubcomponents = componentIndices.length > 1;
        return provider(() -> {
            List<InterfaceComponent> components = new ArrayList<>();
            for (int index : componentIndices) {
                InterfaceComponent component = Interfaces.getComponent(group, index);
                if (component != null) {
                    components.add(component);
                    if (includeSubcomponents) {
                        Collections.addAll(components, component.getComponents());
                    }
                }
            }
            return components;
        });
    }

    public InterfaceComponentQueryBuilder texts(Predicate<String> text) {
        this.text = text;
        return self();
    }

    public InterfaceComponentQueryBuilder names(Predicate<String> name) {
        this.name = name;
        return self();
    }

    public InterfaceComponentQueryBuilder actions(Predicate<String> action) {
        this.action = action;
        return self();
    }

    public InterfaceComponentQueryBuilder materials(int... materials) {
        this.materials = materials;
        return self();
    }

    public InterfaceComponentQueryBuilder types(int... types) {
        this.types = types;
        return self();
    }

    public InterfaceComponentQueryBuilder contentTypes(int... contentTypes) {
        this.contentTypes = contentTypes;
        return self();
    }

    public InterfaceComponentQueryBuilder foregrounds(int... foregrounds) {
        this.foregrounds = foregrounds;
        return self();
    }

    public InterfaceComponentQueryBuilder models(int... modelIds) {
        this.modelIds = modelIds;
        return self();
    }

    public InterfaceComponentQueryBuilder includeSubcomponents() {
        includeSubcomponents = true;
        return self();
    }

    public InterfaceComponentQueryBuilder visible(boolean visible) {
        this.visible = visible;
        return self();
    }

    public InterfaceComponentQueryBuilder visible() {
        return visible(true);
    }

    @Override
    public boolean test(InterfaceComponent cmp) {
        if (visible != null && cmp.isVisible() != visible) {
            return false;
        }

        if (materials != null && !ArrayUtils.contains(materials, cmp.getMaterialId())) {
            return false;
        }

        if (types != null && !ArrayUtils.contains(types, cmp.getType())) {
            return false;
        }

        if (contentTypes != null && !ArrayUtils.contains(contentTypes, cmp.getContentType())) {
            return false;
        }

        if (foregrounds != null && !ArrayUtils.contains(foregrounds, cmp.getTextColor())) {
            return false;
        }

        if (modelIds != null && !ArrayUtils.contains(modelIds, cmp.getModelId())) {
            return false;
        }

        if (text != null && !text.test(cmp.getText())) {
            return false;
        }

        if (name != null && !name.test(cmp.getName())) {
            return false;
        }

        if (action != null && !cmp.containsAction(action)) {
            return false;
        }

        return super.test(cmp);
    }
}
