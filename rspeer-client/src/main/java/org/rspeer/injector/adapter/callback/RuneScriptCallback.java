package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class RuneScriptCallback extends MediatorDelegate<MethodHook> {

    private static final String CALL_DESC = "(L" + PROVIDER_PACKAGE + "RSScriptEvent;)Z";

    public RuneScriptCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("fireScriptEvent"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList stack = new InsnList();
        Label label = new Label();
        LabelNode ln = new LabelNode(label);
        mn.visitLabel(label);
        stack.add(new InsnNode(ICONST_0));
        stack.add(referenceMediator());
        stack.add(new VarInsnNode(ALOAD, 0));
        stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, "fireScriptEvent", CALL_DESC, false));
        stack.add(new JumpInsnNode(IFNE, ln));
        stack.add(new InsnNode(RETURN));
        stack.add(ln);
        mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
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
