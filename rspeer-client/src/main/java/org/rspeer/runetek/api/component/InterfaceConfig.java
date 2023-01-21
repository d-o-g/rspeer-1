package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;

/**
 * @see InterfaceComponent#getConfig()
 */
public final class InterfaceConfig {

    private InterfaceConfig() {
        throw new IllegalAccessError();
    }

    /**
     * This does <b>not</b> work for the old style tutorial island components
     * Note: To differentiate between continue and a chat option, text color can be used.
     *
     * @param config The InterfaceComponent config
     * @return {@code true} if the component is a dialog option. This also includes the continue options
     */
    public static boolean isDialogOption(int config) {
        return (config & 0x1) != 0;
    }

    /**
     * If an option is not allowed, it will not appear in the context menu.
     * I can't think of anything this applies to in Oldschool, however the summoning orb
     * in the main game has a "dismiss" option which isn't shown when a familiar is not summoned.
     *
     * @param config      The InterfaceComponent config
     * @param actionIndex The index of the menu option
     * @return {@code true} if a right click option is enabled or not.
     */
    public static boolean isActionEnabled(int config, int actionIndex) {
        return ((config >> (actionIndex + 1)) & 0x1) != 0;
    }

    /**
     * @param config The InterfaceComponent config
     * @return The application targets (what can the component be used on?).
     * e.g. for spells, strike spells can be cast on npcs and other players.
     * enchant can be cast on interface components, charge orb can be cast on objects
     */
    public static int getSpellTargets(int config) {
        return (config >> 11) & 0x3f;
    }

    /**
     * @param config The InterfaceComponent config
     * @return The script event invocation depth. A higher depth means higher interfaces
     * in the hierarchy will have scripts invoked on them too
     */
    public static int getScriptEventDepth(int config) {
        return (config >> 17) & 0x7;
    }

    public static boolean allowsSpells(int config) {
        return (config >> 21 & 0x1) != 0;
    }

    //bit 20 tells whether the component is draggable or nah
    //bit 21 tells whether to allow spells or nah
    //bit 28 is related to drag and drop but i am not sure what it is
    //bit 29 tells whether to remove on drop or nah
    //bit 30 tells whether to allow item actions or nah
    //bit 31 tells whether to allow item usability or nah
    //TODO bits 22-27

    public static boolean isBitSet(int config, int bit) {
        return ((config >> bit) & 0x1) != 0;
    }

    public static boolean isUsingActionsFromDefinition(int config) {
        return (config >> 30 & 1) != 0;
    }

    public enum ApplicationTarget {

        GROUND_ITEM, NPC, OBJECT,
        OTHER_PLAYER, SELF, INTERFACE_COMPONENT;

        public int getFlags() {
            int flags = 0x1 << ordinal();
            flags &= ~(0x7f << 7);
            return flags;
        }
    }
}
