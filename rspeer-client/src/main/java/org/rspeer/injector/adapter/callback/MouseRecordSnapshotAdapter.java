package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.providers.RSMouseRecorder;

import java.util.Map;
import java.util.function.Predicate;

public final class MouseRecordSnapshotAdapter extends MediatorDelegate<FieldHook> {

    private static final String DESC = "(L" + PROVIDER_PACKAGE + "RSMouseRecorder;" + ")V";

    public MouseRecordSnapshotAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSMouseRecorder.class).getField("timeHistory"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == LASTORE) {
                InsnList instructions = new CodeGenerator().append(referenceMediator())
                        .loadLocal(ALOAD, 0)
                        .invokeVirtual(-1, MEDIATOR, "notifyMouseMotionSnapshot", DESC)
                        .collect();
                mn.instructions.insert(ain, instructions);
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.name.equals("run") && mn.desc.equals("()V");
    }
}
