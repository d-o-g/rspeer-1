package org.rspeer.injector.adapter.generic;

import org.rspeer.injector.hook.FieldHook;

public final class FieldMeta {

    final boolean isStatic;
    final String owner, name, desc, getterName, setterName;

    public FieldMeta(boolean isStatic, String owner, String name, String desc, String getterName, String setterName) {
        this.isStatic = isStatic;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.getterName = getterName;
        this.setterName = setterName;
    }

    public FieldMeta(boolean isStatic, String owner, String name, String desc) {
        this(isStatic, owner, name, desc, getter(name, desc), setter(name));
    }

    public FieldMeta(FieldHook hook) {
        this(hook.isStatic(), hook.getOwner(), hook.getInternalName(), hook.getDesc(),
                getter(hook.getDefinedName(), hook.getDesc()), setter(hook.getDefinedName()));
    }

    private static String getter(String name, String desc) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
        return desc.equals("Z") ? "is" + name : "get" + name;
    }

    private static String setter(String name) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
        return "set" + name;
    }
}