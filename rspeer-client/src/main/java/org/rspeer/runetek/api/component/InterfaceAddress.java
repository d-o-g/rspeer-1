package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.ToBooleanFunction;

import java.util.function.*;

/**
 * Created by Spencer on 25/02/2018.
 */
public final class InterfaceAddress {

    private int root;
    private int component;
    private int subComponent;

    private SupplyOnce<InterfaceComponent> supplier;

    public InterfaceAddress(Supplier<InterfaceComponent> supplier) {
        this(-1, -1, -1);
        this.supplier = new SupplyOnce<>(supplier);
    }

    public InterfaceAddress(int root, int component, int subComponent) {
        this.root = root;
        this.component = component;
        this.subComponent = subComponent;
    }

    public InterfaceAddress(int root, int component) {
        this(root, component, -1);
    }

    public InterfaceAddress(int root) {
        this(root, -1);
    }

    public InterfaceAddress(InterfaceComposite composite) {
        this(composite.getGroup());
    }

    private int map(Supplier<InterfaceComponent> supplier, ToIntFunction<InterfaceComponent> mapper) {
        InterfaceComponent comp = supplier.get();
        return comp == null ? -1 : mapper.applyAsInt(comp);
    }

    private void mapComponentInfo() {
        root = map(supplier, InterfaceComponent::getRootIndex);
        component = map(supplier, x -> x.isGrandchild() ? x.getParentIndex() : x.getIndex());
        subComponent = map(supplier, InterfaceComponent::getComponentIndex);
    }

    public int getRoot() {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }
        return root;
    }

    public int getComponent() {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }
        return component;
    }

    public int getSubComponent() {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }
        return subComponent;
    }

    public InterfaceAddress component(int component) {
        return new InterfaceAddress(root, component, -1);
    }

    public InterfaceAddress subComponent(int subComponent) {
        return new InterfaceAddress(root, component, subComponent);
    }

    public InterfaceAddress subComponent(int component, int subComponent) {
        return new InterfaceAddress(root, component, subComponent);
    }

    @Override
    public String toString() {
        return "InterfaceAddress[group=" + root + ",component=" + component + ",subcomponent=" + subComponent + "]";
    }

    public InterfaceComponent resolve(Consumer<InterfaceComponent> action) {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }

        InterfaceComponent component = Interfaces.lookup(this);
        if (component != null && action != null) {
            action.accept(component);
        }
        return component;
    }

    public <T> T map(Function<InterfaceComponent, T> mapper) {
        InterfaceComponent component = resolve();
        return component != null ? mapper.apply(component) : null;
    }

    public int mapToInt(ToIntFunction<InterfaceComponent> mapper) {
        InterfaceComponent component = resolve();
        return component != null ? mapper.applyAsInt(component) : -1;
    }

    public boolean mapToBoolean(ToBooleanFunction<InterfaceComponent> mapper) {
        InterfaceComponent component = resolve();
        return component != null && mapper.applyAsBoolean(component);
    }

    public void ifPresent(Consumer<InterfaceComponent> action) {
        InterfaceComponent component = resolve();
        if (component != null && action != null) {
            action.accept(component);
        }
    }

    public InterfaceComponent resolve() {
        return resolve(null);
    }

    public boolean isMapped() {
        return supplier == null || supplier.done();
    }

    private class SupplyOnce<T> implements Supplier<T> {

        private final Supplier<T> base;
        private T cached;

        private SupplyOnce(Supplier<T> base) {
            this.base = base;
        }

        @Override
        public T get() {
            return cached != null ? cached : (cached = base.get());
        }

        public boolean done() {
            return cached != null;
        }
    }
}
