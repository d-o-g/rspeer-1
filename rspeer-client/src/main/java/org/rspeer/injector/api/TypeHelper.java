package org.rspeer.injector.api;

import org.objectweb.asm.Type;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.ClassHook;

public final class TypeHelper {

    private TypeHelper() {
        throw new IllegalAccessError();
    }

    public static String toKnownMethodDesc(Modscript modscript, String desc) {
        StringBuilder rebuild = new StringBuilder(desc.substring(1, desc.indexOf(')')));
        if (rebuild.toString().contains(";")) {
            rebuild = new StringBuilder();
            Type[] types = Type.getArgumentTypes(desc);
            for (Type type : types) {
                rebuild.append(toKnownType(modscript, type));
            }
        }
        return "(" + rebuild + ")" + toKnownType(modscript, Type.getReturnType(desc));
    }

    public static String toKnownType(Modscript modscript, Type arg) {
        String type = arg.getDescriptor().replace("[", "").replace(";", "");
        if (!type.startsWith("L") || type.contains("java") || type.contains("/")) {
            type = arg.getDescriptor();
        } else {
            type = type.replace("L", "");
            ClassHook returnType = modscript.classes.get(type);
            if (returnType != null) {
                type = arg.getDescriptor().replace(type, HookAdapter.PROVIDER_PACKAGE + "RS" + returnType.getDefinedName());
            }
        }

        if (type.contains("null")) {
            type = arg.getDescriptor();
        }
        return type;
    }
}
