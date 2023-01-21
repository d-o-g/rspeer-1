package org.rspeer.injector.adapter.callback;


import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.providers.RSNpc;

import java.util.Map;
import java.util.function.Predicate;

public final class NpcSpawnCallback extends MediatorDelegate<ClassHook> {

    private static final String DESC = "(L" + PROVIDER_PACKAGE + "RSNpc;)V";

    public NpcSpawnCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSNpc.class), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ALOAD, 0)
                .invokeVirtual(-1, MEDIATOR, "npcSpawned", DESC)
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getInternalName());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.desc.startsWith("(IIZ") && mn.desc.endsWith("V");
    }
}
