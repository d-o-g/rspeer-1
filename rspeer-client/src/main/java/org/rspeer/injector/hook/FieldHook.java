package org.rspeer.injector.hook;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public final class FieldHook extends Hook {

    private final boolean isStatic;
    private final long multiplier;
    private final boolean longMultiplier;
    private final String owner, internalName, desc;

    public FieldHook(String definedName, String owner, String internalName, String desc, boolean isStatic,
                     boolean longMultiplier, String multiplier) {
        super(definedName);
        this.owner = owner;
        this.internalName = internalName;
        this.desc = desc;
        this.isStatic = isStatic;
        this.longMultiplier = longMultiplier;
        if (longMultiplier) {
            this.multiplier = Long.valueOf(multiplier);
        } else {
            this.multiplier = Integer.valueOf(multiplier);
        }
    }

    public boolean match(FieldInsnNode fin) {
        return owner.equals(fin.owner) && internalName.equals(fin.name) && desc.equals(fin.desc);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public long getMultiplier() {
        return multiplier;
    }

    public String getOwner() {
        return owner;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDesc() {
        return desc;
    }

    public String setterName() {
        String name = Character.toUpperCase(getDefinedName().charAt(0)) + getDefinedName().substring(1);
        return "set" + name;
    }

    public AbstractInsnNode getstatic() {
        return new FieldInsnNode(Opcodes.GETSTATIC, getOwner(), getInternalName(), getDesc());
    }

    public AbstractInsnNode putstatic() {
        return new FieldInsnNode(Opcodes.PUTSTATIC, getOwner(), getInternalName(), getDesc());
    }

    public boolean isLongMultiplier() {
        return longMultiplier;
    }
}
