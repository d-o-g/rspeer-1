package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.runetek.api.input.menu.interaction.MenuAction;
import org.rspeer.runetek.providers.RSClient;

import java.util.Map;
import java.util.function.Predicate;

public final class CrosshairAdapter extends HookAdapter<MethodHook> {

    public CrosshairAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSClient.class).getMethod("processAction"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList setStack = new InsnList();
        Label label = new Label();
        LabelNode ln = new LabelNode(label);
        mn.visitLabel(label);
        setStack.add(new VarInsnNode(ILOAD, 6));
        setStack.add(new VarInsnNode(ILOAD, 7));
        setStack.add(new MethodInsnNode(INVOKESTATIC, MenuAction.class.getName().replace('.', '/'), "isAutomated", "(II)Z", false));
        setStack.add(new JumpInsnNode(IFEQ, ln));
        setStack.add(new IntInsnNode(SIPUSH, -1000));
        setStack.add(new VarInsnNode(ISTORE, 6));
        setStack.add(new IntInsnNode(SIPUSH, -1000));
        setStack.add(new VarInsnNode(ISTORE, 7));
        setStack.add(ln);
        mn.instructions.insertBefore(mn.instructions.getFirst(), setStack);
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.name.equals(hook.getInternalName()) && mn.desc.equals(hook.getDesc());
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getOwner());
    }
}
