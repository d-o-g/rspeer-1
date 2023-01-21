package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class BuildComponentMenuCallback extends MediatorDelegate<MethodHook> {

    private final MethodHook insert;

    public BuildComponentMenuCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("buildComponentMenu"), library);
        if (hook == null) {
            hook = modscript.classes.get("Client").getMethod("processComponentEvents");
        }
        insert = modscript.classes.get("Client").getMethod("insertMenuItem");
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if (hook == null) {
            return;
        }
        if (hook.getDefinedName().equals("buildComponentMenu")) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() == RETURN) {
                    InsnList mod = new CodeGenerator().append(referenceMediator())
                            .invokeVirtual(-1, MEDIATOR, "buildComponentMenu", "()V")
                            .collect();
                    mn.instructions.insertBefore(ain, mod);
                }
            }
            return;
        }

        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == INVOKESTATIC) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals(insert.getOwner()) && min.desc.equals(insert.getDesc())
                        && min.name.equals(insert.getInternalName())) {
                    InsnList mod = new CodeGenerator().append(referenceMediator())
                            .invokeVirtual(-1, MEDIATOR, "buildComponentMenu", "()V")
                            .collect();
                    mn.instructions.insert(ain, mod);
                }
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> hook != null && cn.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> hook != null && m.desc.equals(hook.getDesc()) && m.name.equals(hook.getInternalName());
    }
}
