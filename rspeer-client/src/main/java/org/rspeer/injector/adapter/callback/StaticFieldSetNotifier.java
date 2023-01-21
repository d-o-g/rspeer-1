package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;

/**
 * "Lazy" method of injecting callbacks, always injects at the start of the method.
 */
public final class StaticFieldSetNotifier extends MediatorDelegate<FieldHook> {

    private final String call;

    public StaticFieldSetNotifier(Modscript modscript, Map<String, ClassNode> library, FieldHook hook, String call) {
        super(modscript, hook, library);
        this.call = call;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        //TODO iconst_m1 wont work if the field has a multiplier
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == PUTSTATIC && ain.getPrevious() != null
                    && !(ain.getPrevious().getOpcode() == ICONST_M1
                    || ain.getPrevious().getOpcode() == ACONST_NULL)) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (hook.match(fin)) {
                    InsnList stack = referenceMediator();
                    stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, call, "()V", false));
                    mn.instructions.insert(fin, stack);
                }
            }
        }
    }
}
