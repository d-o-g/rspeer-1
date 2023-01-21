package org.rspeer.injector.hook;

import java.util.ArrayList;
import java.util.List;

public final class ClassHook extends Hook {

    private final String internalName;

    private final List<MethodHook> methods = new ArrayList<>();
    private final List<FieldHook> fields = new ArrayList<>();

    public ClassHook(String definedName, String internalName) {
        super(definedName);
        this.internalName = internalName;
    }

    public String getInternalName() {
        return internalName;
    }

    public FieldHook getField(String definedName) {
        return fields.stream().filter(f -> f.getDefinedName().equals(definedName))
                .findFirst().orElse(null);
    }

    public MethodHook getMethod(String definedName) {
        return methods.stream().filter(m -> m.getDefinedName().equals(definedName))
                .findFirst().orElse(null);
    }

    public List<MethodHook> getMethods() {
        return methods;
    }

    public List<FieldHook> getFields() {
        return fields;
    }

    public void addMember(Hook hook) {
        if (hook instanceof FieldHook) {
            fields.add((FieldHook) hook);
        } else if (hook instanceof MethodHook) {
            methods.add((MethodHook) hook);
        }
    }
}
