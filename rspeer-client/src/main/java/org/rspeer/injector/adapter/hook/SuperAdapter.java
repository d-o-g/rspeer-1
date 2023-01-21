package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.providers.subclass.SubclassTarget;

import java.util.Map;
import java.util.function.Predicate;

public final class SuperAdapter extends HookAdapter<ClassHook> {

    private final SubclassTarget subclass;

    public SuperAdapter(Modscript modscript, ClassHook hook, Map<String, ClassNode> library, SubclassTarget subclass) {
        super(modscript, hook, library);
        this.subclass = subclass;
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        cn.superName = subclass.getSuperType().getName().replace('.', '/');
    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.contains(hook.getDefinedName())) {
                    min.owner = subclass.getSuperType().getName().replace('.', '/');
                    break;
                }
            }
        }
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.name.equals("<init>");
    }
}
