package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class BuildMenuCallback extends MediatorDelegate<MethodHook> {

    public BuildMenuCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("buildMenu"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == RETURN) {
                InsnList mod = new CodeGenerator().append(referenceMediator())
                        .invokeVirtual(-1, MEDIATOR, "buildMenu", "()V")
                        .collect();
                mn.instructions.insertBefore(ain, mod);
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> m.desc.equals(hook.getDesc()) && m.name.equals(hook.getInternalName());
    }
}
