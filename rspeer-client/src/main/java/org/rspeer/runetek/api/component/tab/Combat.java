package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;

import java.util.function.Predicate;

public final class Combat {

    private static final Predicate<String> USE_SPECIAL = x -> x.contains("Special Attack");

    private static final int POISON_VARP = 102;
    private static final int SPECIAL_ATTACK_TOGGLE_VARP = 301;
    private static final int SPECIAL_ATTACK_ENERGY_VARP = 300;
    private static final int AUTO_RETALIATE_VARP = 172;
    private static final int SELECTED_STYLE_VARP = 43;
    private static final int WEAPON_TYPE_VARPBIT = 357;
    private static final int MULTI_VARPBIT = 4605;

    private static final int ATTACK_STYLE_INDEX_BASE = 4;
    private static final int ATTACK_STYLE_INDEX_INCREMENT = 4;

    private static final int SELECTED_MATERIAL_ID = 654;

    private static final int COMBAT_TAB_INTERFACE = InterfaceComposite.COMBAT_TAB.getGroup();

    private static final InterfaceAddress SPECIAL_ATTACK_BUTTON = new InterfaceAddress(
            () -> Interfaces.getFirst(COMBAT_TAB_INTERFACE,
                    x -> x.containsAction(y -> y.contains("Special Attack")))
    );

    private static final InterfaceAddress AUTO_RETALIATE_BUTTON
            = new InterfaceAddress(() -> Interfaces.getFirst(COMBAT_TAB_INTERFACE, comp -> comp.containsAction("Auto retaliate")));

    private static final int[] YIKES = {0, 2, 3, 5, 7, 12, 13, 18, 19, 20, 25, 26};

    private Combat() {
        throw new IllegalAccessError();
    }

    public static WeaponType getWeaponType() {
        int value = Varps.getBitValue(WEAPON_TYPE_VARPBIT);
        WeaponType[] values = WeaponType.values();
        return value >= 0 && value < values.length ? values[value] : null;
    }

    public static AttackStyle getAttackStyle() {
        WeaponType type = getWeaponType();
        if (type == null) {
            return null;
        }

        int value = Varps.get(SELECTED_STYLE_VARP);
        AttackStyle[] values = type.getAttackStyles();
        return value >= 0 && value < values.length ? values[value] : null;
    }

    /**
     * @deprecated Use {@link #getAttackStyle()}
     */
    @Deprecated
    public static int getSelectedStyle() {
        int style = Varps.get(SELECTED_STYLE_VARP);
        int type = Varps.getBitValue(WEAPON_TYPE_VARPBIT);
        if (style == 3) {
            for (int yikes : YIKES) {
                if (type == yikes) {
                    style = 2;
                }
            }
        }
        return style;
    }

    public static boolean select(int style) {
        if (style >= ATTACK_STYLE_INDEX_INCREMENT) {
            return false;
        }

        InterfaceComponent btn = Interfaces.getComponent(COMBAT_TAB_INTERFACE,
                ATTACK_STYLE_INDEX_BASE + ATTACK_STYLE_INDEX_INCREMENT * style);
        return btn != null && btn.interact(x -> true);
    }

    public static boolean isSpecialBarPresent() {
        return SPECIAL_ATTACK_BUTTON.mapToBoolean(InterfaceComponent::isVisible);
    }

    public static int getSpecialEnergy() {
        return Varps.get(SPECIAL_ATTACK_ENERGY_VARP) / 10;
    }

    public static boolean toggleSpecial(boolean active) {
        if (isSpecialActive() == active) {
            return true;
        }
        InterfaceComponent btn = Interfaces.lookup(SPECIAL_ATTACK_BUTTON);
        return btn != null && btn.interact(USE_SPECIAL) && Time.sleepUntil(() -> isSpecialActive() == active, 1200);
    }

    public static boolean isSpecialActive() {
        return Varps.getBoolean(SPECIAL_ATTACK_TOGGLE_VARP);
    }

    public static boolean isAutoRetaliateOn() {
        return !Varps.getBoolean(AUTO_RETALIATE_VARP);
    }

    public static boolean toggleAutoRetaliate(boolean active) {
        if (active == isAutoRetaliateOn()) {
            return true;
        }

        InterfaceComponent retaliate = Interfaces.lookup(AUTO_RETALIATE_BUTTON);
        return retaliate != null && retaliate.click();
    }

    public static boolean isPoisoned() {
        return Varps.get(POISON_VARP) > 0;
    }

    public static boolean isEnvenomed() {
        return Varps.get(POISON_VARP) >= 1000000;
    }

    public static boolean isAntipoisonActive() {
        return Varps.get(POISON_VARP) < 0;
    }

    public static boolean isMulti() {
        return Varps.getBitValue(MULTI_VARPBIT) == 1;
    }

    public enum WeaponType {

        TYPE_0(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE),
        TYPE_1(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE),
        TYPE_2(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE),
        TYPE_3(AttackStyle.ACCURATE, AttackStyle.RAPID, null, AttackStyle.LONGRANGE),
        TYPE_4(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE),
        TYPE_5(AttackStyle.ACCURATE, AttackStyle.RAPID, null, AttackStyle.LONGRANGE),
        TYPE_6(AttackStyle.AGGRESSIVE, AttackStyle.RAPID, AttackStyle.DEFENSIVE_CASTING, null),
        TYPE_7(AttackStyle.ACCURATE, AttackStyle.RAPID, null, AttackStyle.LONGRANGE),
        TYPE_8(AttackStyle.OTHER, AttackStyle.AGGRESSIVE, null, null),
        TYPE_9(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE),
        TYPE_10(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE),
        TYPE_11(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE),
        TYPE_12(AttackStyle.CONTROLLED, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE),
        TYPE_13(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE),
        TYPE_14(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE),
        TYPE_15(AttackStyle.CONTROLLED, AttackStyle.CONTROLLED, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE),
        TYPE_16(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE),
        TYPE_17(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE),
        TYPE_18(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE, AttackStyle.CASTING, AttackStyle.DEFENSIVE_CASTING),
        TYPE_19(AttackStyle.ACCURATE, AttackStyle.RAPID, null, AttackStyle.LONGRANGE),
        TYPE_20(AttackStyle.ACCURATE, AttackStyle.CONTROLLED, null, AttackStyle.DEFENSIVE),
        TYPE_21(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE, AttackStyle.CASTING, AttackStyle.DEFENSIVE_CASTING),
        TYPE_22(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE),
        TYPE_23(AttackStyle.CASTING, AttackStyle.CASTING, null, AttackStyle.DEFENSIVE_CASTING),
        TYPE_24(AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE),
        TYPE_25(AttackStyle.CONTROLLED, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE),
        TYPE_26(AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, null, AttackStyle.AGGRESSIVE),
        TYPE_27(AttackStyle.ACCURATE, null, null, AttackStyle.OTHER);


        private final AttackStyle[] attackStyles;

        WeaponType(AttackStyle... attackStyles) {
            this.attackStyles = attackStyles;
        }

        public AttackStyle[] getAttackStyles() {
            return attackStyles;
        }
    }

    public enum AttackStyle {

        ACCURATE("Accurate", Skill.ATTACK),
        AGGRESSIVE("Aggressive", Skill.STRENGTH),
        DEFENSIVE("Defensive", Skill.DEFENCE),
        CONTROLLED("Controlled", Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE),
        RAPID("Rapid", Skill.RANGED),
        LONGRANGE("Longrange", Skill.RANGED, Skill.DEFENCE),
        CASTING("Casting", Skill.MAGIC),
        DEFENSIVE_CASTING("Defensive Casting", Skill.MAGIC, Skill.DEFENCE),
        OTHER("Other");

        private final String name;
        private final Skill[] skills;

        AttackStyle(String name, Skill... skills) {
            this.name = name;
            this.skills = skills;
        }

        public String getName() {
            return name;
        }

        public Skill[] getSkills() {
            return skills;
        }
    }
}