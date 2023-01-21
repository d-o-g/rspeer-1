package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.InterfaceOptions;
import org.rspeer.runetek.api.component.Interfaces;

import java.util.function.Predicate;

/**
 * Created by Spencer on 03/02/2018.
 */
public enum Tab {

    CLAN_CHAT(31, 35, 35),
    FRIENDS_LIST(33, 37, 37),

    @Deprecated
    IGNORE_LIST(30, 29, 32),

    ACCOUNT_MANAGEMENT(32, 36, 36),
    LOGOUT(34, 29, 38), //not an actual tab for line layout mode, but a button next to minimap
    OPTIONS(35, 38, 39),
    EMOTES(36, 39, 40),
    MUSIC_PLAYER(37, 40, 41),
    COMBAT(48, 50, 51),

    /**
     * @deprecated Renamed to SKILLS
     */
    @Deprecated
    STATS(49, 51, 52),

    SKILLS(49, 51, 52),
    QUEST_LIST(50, 52, 53),
    INVENTORY(51, 53, 54),
    EQUIPMENT(52, 54, 55),
    PRAYER(53, 55, 56),
    MAGIC(54, 56, 57);

    private static final int BOX_LAYOUT_TABS = 161;
    private static final int LINE_LAYOUT_TABS = 164;
    private static final int MAIN_FIXED_MODE = 548;

    // private final int fixedModeIndex, lineLayoutIndex, boxLayoutIndex;

    private final InterfaceAddress fixedModeAddress, lineLayoutAddress, boxLayoutAddress;

    Tab(int fixedModeIndex, int lineLayoutIndex, int boxLayoutIndex) {
        // this.fixedModeIndex = fixedModeIndex;
        // this.lineLayoutIndex = lineLayoutIndex;
        // this.boxLayoutIndex = boxLayoutIndex;

        Predicate<InterfaceComponent> predicate = x -> x.containsAction(
                y -> y.toLowerCase().contains((toString().toLowerCase()))
        );
        fixedModeAddress = new InterfaceAddress(() -> Interfaces.getFirst(MAIN_FIXED_MODE, predicate));
        lineLayoutAddress = new InterfaceAddress(() -> Interfaces.getFirst(LINE_LAYOUT_TABS, predicate));
        boxLayoutAddress = new InterfaceAddress(() -> Interfaces.getFirst(BOX_LAYOUT_TABS, predicate));
    }

    private static String toTitleCase(String givenString) {
        StringBuilder sb = new StringBuilder();
        for (String anArr : givenString.split(" ")) {
            sb.append(Character.toUpperCase(anArr.charAt(0)))
                    .append(anArr.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public int getLineLayoutIndex() {
        return lineLayoutAddress.mapToInt(InterfaceComponent::getIndex);
    }

    /**
     * @return This will only return correctly for resizable box layout.
     * @see #getFixedModeIndex For non resizable
     */
    public int getBoxLayoutIndex() {
        return boxLayoutAddress.mapToInt(InterfaceComponent::getIndex);
    }

    private boolean isBoxLayout() {
        return InterfaceOptions.getTabLayout() == InterfaceOptions.TabLayout.BOX;
    }

    public int getComponentIndex() {
        if (InterfaceOptions.getViewMode() == InterfaceOptions.ViewMode.FIXED_MODE) {
            return fixedModeAddress.mapToInt(InterfaceComponent::getIndex);
        }
        return isBoxLayout() ? boxLayoutAddress.mapToInt(InterfaceComponent::getIndex)
                : lineLayoutAddress.mapToInt(InterfaceComponent::getIndex);
    }

    public InterfaceComponent getComponent() {
        if (InterfaceOptions.getViewMode() == InterfaceOptions.ViewMode.FIXED_MODE) {
            return Interfaces.lookup(fixedModeAddress);
        }
        return Interfaces.lookup(isBoxLayout() ? boxLayoutAddress : lineLayoutAddress);
    }

    public boolean isOpen() {
        InterfaceComponent component = getComponent();
        return component != null && component.getMaterialId() != -1;
    }

    @Override
    public String toString() {
        if (this == STATS) {
            return "Skills";
        } else if (this == OPTIONS) {
            return "Settings";
        } else if (this == COMBAT) {
            return "Combat Options";
        }
        String name = super.name();
        return toTitleCase(name.toLowerCase().replace("_", " "));
    }

    public int getFixedModeIndex() {
        return fixedModeAddress.mapToInt(InterfaceComponent::getIndex);
    }
}
