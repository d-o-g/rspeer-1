package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;
import java.util.function.Predicate;

public final class BuildMenuCallback2 extends MediatorDelegate<FieldHook> {

    public BuildMenuCallback2(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getField("menuRowCount"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == PUTSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (hook.match(fin)) {
                    InsnList mod = new CodeGenerator().append(referenceMediator())
                            .invokeVirtual(-1, MEDIATOR, "buildMenu", "()V")
                            .collect();
                    mn.instructions.insert(fin, mod);
                }
            }
        }
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> !m.name.contains(">");
    }
}
