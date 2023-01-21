package org.rspeer.injector.api;


import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;

public final class ExprHelper {

    private ExprHelper() {
        throw new IllegalAccessError();
    }

    //lazy/shit
    public static String getOperationDesc(CodeGenerator expr) {
        for (AbstractInsnNode ain : expr.collect().toArray()) {
            if (ain instanceof FieldInsnNode) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                return Type.getReturnType(fin.desc).getDescriptor();
            } else if (ain instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) ain;
                return Type.getReturnType(min.desc).getDescriptor();
            } else if (ain instanceof TypeInsnNode) {
                TypeInsnNode tin = (TypeInsnNode) ain;
                return Type.getReturnType(tin.desc).getDescriptor();
            } else if (ain instanceof VarInsnNode) {
                char identifier = Printer.OPCODES[ain.getOpcode()].charAt(0);
                if (identifier == 'L') {
                    identifier = 'J';
                }
                return Type.getReturnType(String.valueOf(identifier)).getDescriptor();
            } else if (ain instanceof LdcInsnNode) {
                LdcInsnNode lin = (LdcInsnNode) ain;
                Class<?> clazz = lin.cst.getClass();
                if (clazz == Integer.class) {
                    return "I";
                } else if (clazz == Byte.class) {
                    return "B";
                } else if (clazz == Boolean.class) {
                    return "Z";
                } else if (clazz == Short.class) {
                    return "S";
                } else if (clazz == Double.class) {
                    return "D";
                } else if (clazz == Float.class) {
                    return "F";
                } else if (clazz == Character.class) {
                    return "C";
                } else if (clazz == Long.class) {
                    return "J";
                } else {
                    return Type.getType(lin.cst.getClass()).getDescriptor();
                }
            }
        }
        return null;
    }
}
