package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.providers.RSWorld;

import java.util.function.Predicate;

/**
 * @author MalikDz
 */
public final class WorldHopper {

    private static final int WORLDS_COMPONENT = 16;
    private static final int WORLD_SWITCHER_COMPONENT = 3;
    private static final int LOGOUT_INTERFACE_INDEX = InterfaceComposite.LOGOUT_TAB.getGroup();
    private static final int WORLDS_INTERFACE_INDEX = InterfaceComposite.WORLD_SELECT.getGroup();

    private WorldHopper() {
        throw new IllegalAccessError();
    }

    public static boolean hopTo(RSWorld world) {
        return world != null && hopTo(world.getId());
    }

    public static boolean hopTo(Predicate<? super RSWorld> predicate) {
        RSWorld[] worlds = Worlds.getLoaded(predicate);
        return worlds.length > 0 && hopTo(worlds[0]);
    }

    public static boolean hopNext(Predicate<? super RSWorld> predicate) {
        RSWorld[] worlds = Worlds.getLoaded(predicate);
        int current = Worlds.getCurrent();
        for (RSWorld world : worlds) {
            if (world.getId() > current) {
                return hopTo(world);
            }
        }
        return worlds.length > 0 && hopTo(worlds[0]);
    }

    public static boolean hopPrevious(Predicate<? super RSWorld> predicate) {
        RSWorld[] worlds = Worlds.getLoaded(predicate);
        int current = Worlds.getCurrent();
        for (RSWorld world : worlds) {
            if (world.getId() < current) {
                return hopTo(world);
            }
        }
        return worlds.length > 0 && hopTo(worlds[worlds.length - 1]);
    }

    public static boolean randomHopInF2p() {
        return randomHop(w -> !w.isMembers() && !w.isPVP() && !w.isSkillTotal());
    }

    public static boolean randomHopInP2p() {
        return randomHop(w -> w.isMembers() && !w.isPVP() && !w.isSkillTotal() && !w.isLastManStanding());
    }

    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.getComponent(WORLDS_INTERFACE_INDEX, WORLDS_COMPONENT);
        return component != null && Tabs.isOpen(Tab.LOGOUT) && component.isVisible();
    }

    public static boolean randomHop(Predicate<? super RSWorld> predicate) {
        RSWorld[] worlds = Worlds.getLoaded(x -> x.getId() != Worlds.getCurrent() && predicate.test(x));
        return worlds.length > 0 && hopTo(worlds[(int) (Math.random() * (worlds.length - 1))]);
    }

    public static boolean hopTo(int worldId) {
        if (worldId <= 300) {
            worldId += 300;
        }

        if (!open()) {
            return false;
        }

        if (Dialog.isViewingChatOptions()
                && Dialog.getChatOptions().length >= 2) {
            Dialog.process(1);
            return false;
        }

        if (!isOpen()) {
            return false;
        }

        InterfaceComponent comp = Interfaces.getComponent(WORLDS_INTERFACE_INDEX, WORLDS_COMPONENT, worldId);
        return comp != null && comp.click();
    }

    public static boolean open() {
        if (isOpen()) {
            return true;
        } else if (Tabs.isOpen(Tab.LOGOUT) && !isOpen()) {
            InterfaceComponent comp = Interfaces.getComponent(LOGOUT_INTERFACE_INDEX, WORLD_SWITCHER_COMPONENT);
            return comp != null && comp.click();
        } else if (!Tabs.isOpen(Tab.LOGOUT) && Tabs.open(Tab.LOGOUT)) {
            InterfaceComponent cmp = Interfaces.getComponent(LOGOUT_INTERFACE_INDEX, WORLD_SWITCHER_COMPONENT);
            return isOpen() || cmp != null && cmp.click();
        }
        return false;
    }
}
