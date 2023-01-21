package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.predicate.Predicates;

/**
 * Created by Spencer on 03/02/2018.
 */
public final class Tabs {

    private Tabs() {
        throw new IllegalAccessError();
    }

    /**
     * @return The current open tab.
     */
    public static Tab getOpen() {
        for (Tab tab : Tab.values()) {
            if (tab.isOpen()) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Gets the {@link InterfaceComponent} of all the tabs.
     *
     * @return All the tabs {@link InterfaceComponent}'s
     */
    public static InterfaceComponent[] asComponents() {
        Tab[] values = Tab.values();
        InterfaceComponent[] tabs = new InterfaceComponent[values.length];
        for (int i = 0; i < values.length; i++) {
            tabs[i] = values[i].getComponent();
        }
        return tabs;
    }

    /**
     * Opens the provided tab.
     *
     * @param tab The tab to open.
     * @return {@code true} if the tab was successfully opened or already opened
     */
    public static boolean open(Tab tab) {
        if (Tabs.getOpen() == tab) {
            return true;
        }
        InterfaceComponent component = tab.getComponent();
        return component != null && component.interact(Predicates.always());
    }

    public static boolean isOpen(Tab tab) {
        return tab.isOpen();
    }
}
