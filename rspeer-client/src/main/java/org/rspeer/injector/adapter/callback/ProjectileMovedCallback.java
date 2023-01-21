package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.providers.RSProjectile;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Predicate;

public final class ProjectileMovedCallback extends MediatorDelegate<ClassHook> {

    private static final String DESC = "(L" + PROVIDER_PACKAGE + "RSProjectile;II)V";

    public ProjectileMovedCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSProjectile.class), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(Opcodes.ALOAD, 0)
                .loadLocal(Opcodes.ILOAD, 1)
                .loadLocal(Opcodes.ILOAD, 2)
                .invokeVirtual(-1, MEDIATOR, "projectileMoved", DESC)
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getInternalName());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> !Modifier.isStatic(mn.access)
                && !mn.name.equals("<init>")
                && mn.desc.startsWith("(III") && mn.desc.endsWith("V");
    }
}
