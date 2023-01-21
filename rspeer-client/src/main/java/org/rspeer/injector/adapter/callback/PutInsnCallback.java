package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.TypeHelper;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Converts a putfield/putstatic instruction to an invokevirtual
 * Creates a new method which the invokevirtual references,
 * this method controls the callback.
 */
public final class PutInsnCallback extends MediatorDelegate<FieldHook> {

    private Predicate<String> ownerPredicate = x -> false;

    public PutInsnCallback(Modscript modscript, Map<String, ClassNode> library, FieldHook hook) {
        super(modscript, hook, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        if (cn.name.equals(hook.isStatic() ? "client" : hook.getOwner())) {
            MethodNode mn = new MethodNode(ACC_PUBLIC, hook.setterName(), "(" + hook.getDesc() + ")V", null, null);
            if (hook.isStatic()) {
                mn.access |= ACC_STATIC;
            }
            InsnList stack = new InsnList();
            stack.add(referenceMediator());
            String desc = "(" + hook.getDesc() + ")V";
            if (!hook.isStatic()) {
                String parent = TypeHelper.toKnownType(modscript, Type.getType("L" + cn.name + ";"));
                desc = "(" + parent + hook.getDesc() + ")V";
                stack.add(new VarInsnNode(ALOAD, 0));
            }
            stack.add(new VarInsnNode(Type.getType(hook.getDesc()).getOpcode(ILOAD), hook.isStatic() ? 0 : 1));
            if (hook.getMultiplier() != 0) {
                stack.add(new LdcInsnNode((int) hook.getMultiplier()));
                stack.add(new InsnNode(IMUL));
            }
            stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, hook.setterName(), desc, false));
            if (!hook.isStatic()) {
                stack.add(new VarInsnNode(ALOAD, 0));
            }
            stack.add(new VarInsnNode(Type.getType(hook.getDesc()).getOpcode(ILOAD), hook.isStatic() ? 0 : 1));
            stack.add(new FieldInsnNode(hook.isStatic() ? PUTSTATIC : PUTFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
            stack.add(new InsnNode(RETURN));
            mn.instructions = stack;
            cn.methods.add(mn);
        }
    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if (mn.name.equals(hook.setterName()) || mn.name.contains("<")) {
            return;
        }
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && (ain.getOpcode() == PUTFIELD || ain.getOpcode() == PUTSTATIC)) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (matches(fin)) {
                    MethodInsnNode min = new MethodInsnNode(hook.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL, hook.isStatic() ? "client" : hook.getOwner(), hook.setterName(), "(" + hook.getDesc() + ")V", false);
                    mn.instructions.set(fin, min);
                }
            }
        }
    }

    private boolean matches(FieldInsnNode fin) {
        return hook.getInternalName().equals(fin.name)
                && hook.getDesc().equals(fin.desc)
                && (hook.getOwner().equals(fin.owner) || ownerPredicate.test(fin.owner));
    }

    public PutInsnCallback setOwnerPredicate(Predicate<String> ownerPredicate) {
        this.ownerPredicate = ownerPredicate;
        return this;
    }
}
