package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class LoginResponseCallback extends MediatorDelegate<MethodHook> {

    public LoginResponseCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("setLoginMessages"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ILOAD, 0)
                .invokeVirtual(-1, MEDIATOR, "processLoginResponse", "(I)V")
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> {
            int count = 0;
            if (m.desc.endsWith("V") && m.desc.startsWith("(I") && (m.access & ACC_STATIC) > 0) {
                for (AbstractInsnNode ain : m.instructions.toArray()) {
                    if (ain != null && ain.getOpcode() == INVOKESTATIC) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (min.owner.equals(hook.getOwner()) && min.desc.equals(hook.getDesc())
                                && min.name.equals(hook.getInternalName())) {
                            count++;
                        }
                    }
                }
            }
            return count > 20;
        };
    }
}
