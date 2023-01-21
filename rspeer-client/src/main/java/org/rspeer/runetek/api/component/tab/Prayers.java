package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Man16
 * @author Spencer
 */
public final class Prayers {

    private static final int ACTIVE_PRAYERS_VARP = 83;
    private static final int QUICK_PRAYER_VARPBIT = 4103;

    private static final InterfaceAddress QUICK_PRAYER_ADDRESS = new InterfaceAddress(160, 14);

    private Prayers() {
        throw new IllegalAccessError();
    }

    private static InterfaceComponent getComponent(Prayer prayer) {
        return Interfaces.lookup(prayer.getComponentAddress());
    }

    public static boolean flick(Prayer prayer, int delay) {
        if (isActive(prayer)) {
            return false;
        }
        InterfaceComponent component = Interfaces.lookup(prayer.getComponentAddress());
        if (component == null) {
            return false;
        }
        if (component.interact("Activate")) {
            Time.sleep(delay);
            return component.interact("Deactivate");
        }
        return false;
    }

    public static Prayer[] getActive() {
        int value = Varps.get(ACTIVE_PRAYERS_VARP);
        if (value == 0) {
            return new Prayer[0];
        }

        List<Prayer> active = new ArrayList<>();
        for (Prayer prayer : Prayer.values()) {
            if ((value & prayer.getVarpValue()) != 0) {
                active.add(prayer);
            }
        }
        return active.toArray(new Prayer[0]);
    }

    public static boolean isActive(Prayer prayer) {
        return isActive(new Prayer[]{prayer});
    }

    public static boolean isActive(Prayer... prayers) {
        for (Prayer prayer : prayers) {
            if ((Varps.get(ACTIVE_PRAYERS_VARP) & prayer.getVarpValue()) == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isQuickPrayerActive() {
        return Varps.getBitValue(QUICK_PRAYER_VARPBIT) == 1;
    }

    public static boolean toggle(boolean on, Prayer prayer) {
        InterfaceComponent component = getComponent(prayer);
        return component != null && component.interact(on ? "Activate" : "Deactivate");
    }

    public static boolean toggleQuickPrayer(boolean on) {
        InterfaceComponent component = Interfaces.lookup(QUICK_PRAYER_ADDRESS);
        return component != null && component.interact(on ? "Activate" : "Deactivate");
    }

    public static int getPoints() {
        return Skills.getCurrentLevel(Skill.PRAYER);
    }

    public static boolean isUnlocked(Prayer prayer) {
        if (Skills.getLevel(Skill.PRAYER) < prayer.getLevel()) {
            return false;
        }

        if (prayer.getUnlockedMaterialId() == -1) {
            return true;
        }

        InterfaceComponent btn = Interfaces.lookup(prayer.getComponentAddress());
        return btn != null
                && btn.getComponent(x -> x.getMaterialId() == prayer.getUnlockedMaterialId()) != null;
    }
}
