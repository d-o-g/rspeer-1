package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;

/**
 * Created by Yasper on 11/08/18.
 */
public final class HouseOptions {

    private static final int GROUP = InterfaceComposite.HOUSE_OPTIONS.getGroup();

    private static final InterfaceAddress OPTIONS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(InterfaceComposite.OPTIONS_TAB.getGroup(), x -> x.containsAction("View House Options"))
    );

    private static final InterfaceAddress BUILDING_MODE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("On"))
    );

    private static final InterfaceAddress CALL_SERVANT = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP, x -> x.containsAction("Call Servant"))
    );

    private HouseOptions() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        return getBuildingModeButton() != null;
    }

    public static boolean setBuildingMode(boolean enabled) {
        if (!isOpen()) {
            return false;
        }

        InterfaceComponent button;
        if (enabled) {
            button = getBuildingModeButton();
        } else {
            button = Interfaces.getComponent(BUILDING_MODE_ADDRESS.getRoot(), BUILDING_MODE_ADDRESS.getComponent() + 1);
        }

        return button != null && button.click();
    }

    public static boolean callButler() {
        if (!isOpen()) {
            return false;
        }

        InterfaceComponent call = Interfaces.lookup(CALL_SERVANT);
        return call != null && call.click();
    }

    public static boolean open() {
        if (isOpen()) {
            return true;
        }

        InterfaceComponent button = getButton();
        return button != null
                && Tabs.open(Tab.OPTIONS)
                && button.click()
                && Time.sleepUntil(HouseOptions::isOpen, 1200);
    }

    private static InterfaceComponent getButton() {
        return Interfaces.lookup(OPTIONS_ADDRESS);
    }

    private static InterfaceComponent getBuildingModeButton() {
        return Interfaces.lookup(BUILDING_MODE_ADDRESS);
    }
}
