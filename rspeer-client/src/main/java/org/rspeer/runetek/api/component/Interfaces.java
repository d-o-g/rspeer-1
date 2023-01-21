package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.query.InterfaceComponentQueryBuilder;
import org.rspeer.runetek.providers.RSIntegerNode;
import org.rspeer.runetek.providers.RSInterfaceComponent;
import org.rspeer.runetek.providers.RSInterfaceNode;
import org.rspeer.runetek.providers.RSNodeTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Created by MadDev on 11/19/17.
 */
public final class Interfaces {

    private static final int[] CLOSE_BUTTON_MATERIAL = {535, 831};

    private static final BiPredicate<InterfaceComponent, Predicate<String>> ACTION_PREDICATE = (component, input) -> {
        String[] actions = component.getActions();
        for (String action : actions) {
            if (action != null && input.test(action)) {
                return true;
            }
        }
        return false;
    };

    private static final BiPredicate<InterfaceComponent, int[]> MATERIAL_PREDICATE = (component, materials) -> {
        for (int material : materials) {
            if (component.getMaterialId() == material) {
                return true;
            }
        }
        return false;
    };

    private Interfaces() {
        throw new IllegalAccessError();
    }

    public static int getRootIndex() {
        return Game.getClient().getRootInterfaceIndex();
    }

    private static RSInterfaceComponent[][] getRaw() {
        return Game.getClient().getInterfaces();
    }

    public static boolean validate(RSInterfaceComponent[][] raw, int group) {
        return group >= 0 && raw != null && raw.length >= group && raw[group] != null;
    }

    public static boolean validateComponent(RSInterfaceComponent[] container, int component) {
        return component >= 0 && container != null && container.length > 0 && container.length >= component && container[component] != null;
    }

    public static boolean validateComponent(int group, int component) {
        return validateComponent(get(group), component);
    }

    /**
     * @param group                The parent index
     * @param predicate            The predicate used to filter the components
     * @param includeGrandchildren whether or not to include grandchildren in the search
     * @return An array of components under the given parent index, matching the given predicate
     */
    public static InterfaceComponent[] get(int group, Predicate<? super InterfaceComponent> predicate, boolean includeGrandchildren) {
        RSInterfaceComponent[][] raw = getRaw();
        if (!validate(raw, group)) {
            return new InterfaceComponent[0];
        }

        RSInterfaceComponent[] comps = raw[group];
        if (comps == null) {
            return new InterfaceComponent[0];
        }

        List<InterfaceComponent> wrapped = new ArrayList<>();
        for (RSInterfaceComponent rawComp : comps) {
            if (rawComp != null) {
                InterfaceComponent wrapper = rawComp.getWrapper();
                if (predicate.test(wrapper)) {
                    wrapped.add(wrapper);
                }

                if (includeGrandchildren) {
                    for (InterfaceComponent component : wrapper.getComponents()) {
                        if (predicate.test(component)) {
                            wrapped.add(component);
                        }
                    }
                }
            }
        }
        return wrapped.toArray(new InterfaceComponent[0]);
    }

    /**
     * @param group     The parent index
     * @param predicate The predicate used to filter the components
     *                  Note: Does not include grandchildren in this search
     * @return An array of components under the given parent index, matching the given predicate
     */
    public static InterfaceComponent[] get(int group, Predicate<? super InterfaceComponent> predicate) {
        return get(group, predicate, false);
    }

    /**
     * @param group The parent index
     * @return All components under the given parent index
     */
    public static InterfaceComponent[] get(int group) {
        return get(group, Predicates.always());
    }

    public static InterfaceComponent getFirst(int group, Predicate<? super InterfaceComponent> predicate, boolean includeGrandchildren) {
        for (InterfaceComponent layer : get(group)) {
            if (predicate.test(layer)) {
                return layer;
            }

            if (includeGrandchildren) {
                InterfaceComponent component = layer.getComponent(predicate);
                if (component != null) {
                    return component;
                }
            }
        }
        return null;
    }

    public static InterfaceComponent getFirst(int[] group, Predicate<? super InterfaceComponent> predicate, boolean includeGrandchildren) {
        for (int index : group) {
            InterfaceComponent found = getFirst(index, predicate, includeGrandchildren);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public static InterfaceComponent getFirst(int group, Predicate<? super InterfaceComponent> predicate) {
        return getFirst(group, predicate, false);
    }

    /**
     * @param group    The parent index
     * @param component The component index
     * @return An InterfaceComponent with the given parent and componenti ndex
     */
    public static InterfaceComponent getComponent(int group, int component) {
        RSInterfaceComponent[][] raw = getRaw();
        if (!validate(raw, group)) {
            return null;
        }

        RSInterfaceComponent[] container = raw[group];
        if (!validateComponent(container, component)) {
            return null;
        }

        return container[component].getWrapper();
    }

    /**
     * @param group    The parent index
     * @param component The component index
     * @param predicate The predicate to filter the components
     * @return The first InterfaceComponent grandchild matching the predicate
     */
    public static InterfaceComponent getFirst(int group, int component, Predicate<? super InterfaceComponent> predicate) {
        InterfaceComponent comp = getComponent(group, component);
        return comp != null ? comp.getComponent(predicate) : null;
    }

    /**
     * @param group    The parent index
     * @param component The component index
     * @param rest      Any further level indices
     * @return An InterfaceComponent matching the given indices
     */
    public static InterfaceComponent getComponent(int group, int component, int... rest) {
        InterfaceComponent layer = getComponent(group, component);
        if (layer == null) {
            return null;
        }

        for (int index : rest) {
            InterfaceComponent comp = layer.getComponent(index);
            if (comp == null) {
                return null;
            }
            layer = comp;
        }
        return layer;
    }

    /**
     * @param predicate The predicate to select the components
     * @return All InterfaceComponnet's matching the predicate
     */
    public static InterfaceComponent[] get(Predicate<? super InterfaceComponent> predicate) {
        RSInterfaceComponent[][] raw = getRaw();
        if (raw == null) {
            return new InterfaceComponent[0];
        }

        List<InterfaceComponent> components = new ArrayList<>();
        for (int i = 0; i < raw.length; i++) {
            Collections.addAll(components, get(i, predicate, true));
        }
        return components.toArray(new InterfaceComponent[0]);
    }

    /**
     * @deprecated
     * @param predicate The predicate to select the component
     * @return The first InterfaceComponent matching the given predicate
     */
    public static InterfaceComponent getFirst(Predicate<? super InterfaceComponent> predicate) {
        InterfaceComponent[] components = get(predicate);
        return components.length > 0 ? components[0] : null;
    }

    /**
     * @deprecated
     * @param predicate A Predicate used to filter text
     * @return All InterfaceComponent's matching the given text predicate
     */
    public static InterfaceComponent[] filterByText(Predicate<String> predicate) {
        return get(component -> predicate.test(component.getText()));
    }

    /**
     * @deprecated
     * @param predicate A Predicate used to filter text
     * @return The first InterfaceComponent matching the given text predicate
     */
    public static InterfaceComponent firstByText(Predicate<String> predicate) {
        return getFirst(component -> predicate.test(component.getText()));
    }

    /**
     * @deprecated
     * @param predicate A Predicate used to filter actions
     * @return All InterfaceComponent's matching the given action predicate
     */
    public static InterfaceComponent[] filterByAction(Predicate<String> predicate) {
        return get(component -> ACTION_PREDICATE.test(component, predicate));
    }

    /**
     * @deprecated
     * @param predicate A Predicate used to filter actions
     * @return The first InterfaceComponent matching the given action predicate
     */
    public static InterfaceComponent firstByAction(Predicate<String> predicate) {
        return getFirst(component -> ACTION_PREDICATE.test(component, predicate));
    }

    /**
     * @deprecated
     * @param materials The material ids (also known as texture ids) to search for
     * @return All InterfaceComponent's matching the given materials
     */
    public static InterfaceComponent[] filterByMaterial(int... materials) {
        return get(component -> MATERIAL_PREDICATE.test(component, materials));
    }

    /**
     * @deprecated
     * @param materials The material ids (also known as texture ids) to search for
     * @return The first InterfaceComponent matching the given materials
     */
    public static InterfaceComponent firstByMaterial(int... materials) {
        return getFirst(component -> MATERIAL_PREDICATE.test(component, materials));
    }

    /**
     * @return All InterfaceComponent's, unfiltered, indexing retained
     */
    public static InterfaceComponent[][] getAll() {
        RSInterfaceComponent[][] raw = getRaw();
        if (raw == null) {
            return new InterfaceComponent[0][];
        }
        int l = raw.length;
        InterfaceComponent[][] interfaceComponents = new InterfaceComponent[l][];
        for (int i = 0; i < l; i++) {
            interfaceComponents[i] = get(i);
        }
        return interfaceComponents;
    }

    /**
     * Note: This function only returns correctly for "non-default" interfaces. A default interface
     * is one that is always open, such as the inventory or chatbox.
     *
     * @param group The index to check
     * @return {@code true} if the interface at the given index is open
     */
    public static boolean isOpen(int group) {
        for (RSInterfaceNode wn : getOpen()) {
            if (wn.getId() == group) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOpen(InterfaceAddress address) {
        return isOpen(address.getRoot());
    }

    public static boolean isVisible(int group, int component, int... subcomponents) {
        InterfaceComponent c = getComponent(group, component, subcomponents);
        return c != null && c.isVisible();
    }

    public static boolean isVisible(InterfaceAddress address) {
        return address.mapToBoolean(InterfaceComponent::isVisible);
    }

    public static RSNodeTable<RSInterfaceNode> getNodes() {
        return Game.getClient().getInterfaceNodes();
    }

    /**
     * Note: This function only returns correctly for "non-default" interfaces. A default interface
     * is one that is always open, such as the inventory or chatbox.
     *
     * @return All open interfaces
     */
    public static List<RSInterfaceNode> getOpen() {
        List<RSInterfaceNode> open = new ArrayList<>();
        RSNodeTable<RSInterfaceNode> table = getNodes();
        for (RSInterfaceNode node : table.toList()) {
            if (node.getState() != 0 && node.getState() != 3) {
                continue;
            }
            open.add(node);
        }
        return open;
    }

    /**
     * Attempts to close any "non-default" interfaces that are currently open
     * @return {@code true} if successfully closed interfaces
     */
    public static boolean closeAll() {
        List<RSInterfaceNode> open = getOpen();
        if (open.size() == 0) {
            return true;
        }

        boolean closed = true;
        for (RSInterfaceNode node : open) {
            InterfaceComponent closeable = Interfaces.getFirst(node.getId(),
                    x -> MATERIAL_PREDICATE.test(x, CLOSE_BUTTON_MATERIAL)
                            && x.containsAction("Close"),
                    true
            );
            if (closeable != null) {
                closed &= closeable.interact("Close");
            }
        }
        return closed;
    }

    /**
     * @deprecated
     * @return The click here to continue button
     */
    public static InterfaceComponent getContinue() {
        InterfaceComponent cmp = Interfaces.firstByText(x -> x.toLowerCase().contains("click here to continue"));
        return cmp != null && InterfaceConfig.isDialogOption(cmp.getConfig()) ? cmp : null;
    }

    public static InterfaceComponent lookup(InterfaceAddress address) {
        if (address == null || address.getComponent() == -1) {
            return null; //Root reference
        }

        if (address.getSubComponent() != -1) {
            return getComponent(address.getRoot(), address.getComponent(), address.getSubComponent());
        }

        return getComponent(address.getRoot(), address.getComponent());
    }

    public static InterfaceComponentQueryBuilder newQuery() {
        return new InterfaceComponentQueryBuilder();
    }
}
