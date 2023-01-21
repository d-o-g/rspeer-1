package org.rspeer.injector.adapter.callback;


import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class SetWorldCallback extends MediatorDelegate<MethodHook> {

    private static final String SET_WORLD_DESC = "(L" + PROVIDER_PACKAGE + "RSWorld;)V";

    public SetWorldCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("setWorld"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ALOAD, 0)
                .invokeVirtual(-1, MEDIATOR, "worldChanged", SET_WORLD_DESC)
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.desc.equals(hook.getDesc()) && mn.name.equals(hook.getInternalName());
    }
}
