package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;

/**
 * @author Man16
 * @author Spencer
 */
public enum Prayer {

    THICK_SKIN(1, 0x1),
    BURST_OF_STRENGTH(4, 0x2),
    CLARITY_OF_THOUGHT(7, 0x4),
    SHARP_EYE(8, 0x40000),
    MYSTIC_WILL(9, 0x80000),
    ROCK_SKIN(10, 0x8),
    SUPERHUMAN_STRENGTH(13, 0x10),
    IMPROVED_REFLEXES(16, 0x20),
    RAPID_RESTORE(19, 0x40),
    RAPID_HEAL(22, 0x80),
    PROTECT_ITEM(25, 0x100),
    HAWK_EYE(26, 0x100000),
    MYSTIC_LORE(72, 0x200000),
    STEEL_SKIN(28, 0x200),
    ULTIMATE_STRENGTH(31, 0x400),
    INCREDIBLE_REFLEXES(34, 0x800),
    PROTECT_FROM_MAGIC(37, 0x1000),
    PROTECT_FROM_MISSILES(40, 0x2000),
    PROTECT_FROM_MELEE(43, 0x4000),
    EAGLE_EYE(44, 0x400000),
    MYSTIC_MIGHT(45, 0x800000),
    RETRIBUTION(46, 0x8000),
    REDEMPTION(49, 0x10000),
    SMITE(52, 0x20000),
    PRESERVE(55, 0x10000000, 947), //951 = locked
    CHIVALRY(60, 0x2000000),
    PIETY(70, 0x4000000),
    RIGOUR(74, 0x1000000, 1420), //1424 = locked
    AUGURY(77, 0x8000000, 1421); //1425 = locked

    private static final int PARENT_INDEX = 541;

    private final int level;
    private final int varpValue;
    private final int unlockedMaterialId;

    private final InterfaceAddress address;

    Prayer(int level, int varpValue, int unlockedMaterialId) {
        this.level = level;
        this.varpValue = varpValue;
        this.unlockedMaterialId = unlockedMaterialId;
        address = new InterfaceAddress(() -> Interfaces.getFirst(PARENT_INDEX,
                x -> x.getName().toLowerCase().contains(toString().toLowerCase())));
    }

    Prayer(int level, int varpValue) {
        this(level, varpValue, -1);
    }

    public static int getParentIndex() {
        return PARENT_INDEX;
    }

    public int getLevel() {
        return level;
    }

    public int getVarpValue() {
        return varpValue;
    }

    public InterfaceAddress getComponentAddress() {
        return address;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }

    public int getUnlockedMaterialId() {
        return unlockedMaterialId;
    }
}
