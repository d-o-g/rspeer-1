package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.api.TypeHelper;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;
import java.util.function.Function;

public final class TypedDequeInsertionCallback extends MediatorDelegate<FieldHook> {

    private final ClassHook deque;
    private final ClassHook node;
    private final String callName;
    private final String callDesc;

    public TypedDequeInsertionCallback(Modscript modscript,
            Function<Modscript, FieldHook> field,
            ClassHook type,
            String callName,
            Map<String, ClassNode> library) {
        super(modscript, field.apply(modscript), library);
        deque = modscript.classes.get("NodeDeque");
        node = modscript.classes.get("Node");
        this.callName = callName;
        callDesc = "(" + TypeHelper.toKnownType(modscript, Type.getType("L" + type.getInternalName() + ";")) + ")V";
        if (deque == null || node == null) {
            throw new IllegalStateException();
        }
    }

    private static AbstractInsnNode backtrack(AbstractInsnNode ain, int insn) {
        if (ain == null) {
            return null;
        }

        for (int i = 0; i < 5 && (ain = ain.getPrevious()) != null; i++) {
            if (ain.getOpcode() == insn) {
                return ain;
            }
        }
        return null;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == INVOKEVIRTUAL) {
                MethodInsnNode invoke = (MethodInsnNode) ain;
                if (invoke.owner.equals(deque.getInternalName())
                        && invoke.desc.startsWith("(L" + node.getInternalName() + ";")) {
                    FieldInsnNode ctx = (FieldInsnNode) backtrack(invoke, hook.isStatic() ? GETSTATIC : GETFIELD);
                    if (ctx != null && hook.match(ctx)) {
                        VarInsnNode lv = (VarInsnNode) backtrack(invoke, ALOAD);
                        if (lv != null) {
                            InsnList instructions = new CodeGenerator().append(referenceMediator())
                                    .loadLocal(ALOAD, lv.var)
                                    .invokeVirtual(-1, MEDIATOR, callName, callDesc)
                                    .collect();
                            mn.instructions.insert(invoke, instructions);
                        }
                    }
                }
            }
        }
    }
}
