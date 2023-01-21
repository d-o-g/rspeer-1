package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;

public final class CollisionCallback extends MediatorDelegate<FieldHook> {

    public CollisionCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("CollisionMap").getField("flags"), library);
    }

    private static AbstractInsnNode backtrack(AbstractInsnNode ain, int insn) {
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
            if (ain != null && ain.getOpcode() == IASTORE) {
                AbstractInsnNode t = backtrack(ain, IALOAD);
                if (t != null && (t = backtrack(t, AALOAD)) != null && (t = backtrack(t, GETFIELD)) != null) {
                    FieldInsnNode ref = (FieldInsnNode) t;
                    if (!ref.owner.equals(hook.getOwner()) || !ref.name.equals(hook.getInternalName())) {
                        continue;
                    }
                    InsnList stack = new CodeGenerator().append(referenceMediator())
                            .invokeVirtual(-1, MEDIATOR, "notifyCollisionUpdate", "()V")
                            .collect();
                    mn.instructions.insert(ain, stack);
                }
            }
        }
    }
}
