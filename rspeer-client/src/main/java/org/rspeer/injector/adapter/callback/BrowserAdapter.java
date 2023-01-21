package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;

import java.util.Map;
import java.util.function.Predicate;

public final class BrowserAdapter extends MediatorDelegate {

    public BrowserAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == INVOKEVIRTUAL) {
                MethodInsnNode call = (MethodInsnNode) ain;
                if (!call.owner.contains("Desktop") || !call.name.equals("browse")) {
                    continue;
                }
                InsnList stack = new InsnList();
                Label label = new Label();
                LabelNode ln = new LabelNode(label);
                mn.visitLabel(label);
                stack.add(referenceMediator());
                stack.add(new VarInsnNode(ALOAD, 0));
                stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, "canBrowse", "(Ljava/lang/String;)Z", false));
                stack.add(new JumpInsnNode(IFNE, ln));
                stack.add(new InsnNode(RETURN));
                stack.add(ln);

                AbstractInsnNode location = ain;
                for (int i = 0; i < 10 && location != null; i++) {
                    if (location.getOpcode() == NEW) {
                        mn.instructions.insertBefore(location, stack);
                        return;
                    }
                    location = location.getPrevious();
                }
            }
        }
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.desc.startsWith("(Ljava/lang/String;Z") && (mn.access & ACC_STATIC) > 0;
    }
}
