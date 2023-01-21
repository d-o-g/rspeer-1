package org.rspeer.injector.adapter.hook;


import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.runetek.api.input.menu.interaction.MenuAction;
import org.rspeer.runetek.providers.RSClient;

import java.util.Map;
import java.util.function.Predicate;

public final class ProcessActionSetter extends HookAdapter<MethodHook> {

    private final FieldHook clientInstance;

    public ProcessActionSetter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSClient.class).getMethod("processAction"), library);
        clientInstance = modscript.classes.get("Client").getField("instance");
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
        setStack.add(clientInstance.getstatic());
        setStack.add(new MethodInsnNode(INVOKEVIRTUAL, "client", "isForcingInteraction", "()Z", false));
        setStack.add(new JumpInsnNode(IFEQ, ln));
        setStack.add(invokeAndStoreInt("getSecondaryArg", 0));
        setStack.add(invokeAndStoreInt("getTertiaryArg", 1));
        setStack.add(invokeAndStoreInt("getOpcode", 2));
        setStack.add(invokeAndStoreInt("getPrimaryArg", 3));
        setStack.add(new IntInsnNode(SIPUSH, -1000));
        setStack.add(new VarInsnNode(ISTORE, 6));
        setStack.add(new IntInsnNode(SIPUSH, -1000));
        setStack.add(new VarInsnNode(ISTORE, 7));
        setStack.add(ln);
        mn.instructions.insertBefore(mn.instructions.getFirst(), setStack);
    }

    private InsnList invokeAndStoreInt(String function, int local) {
        InsnList stack = new InsnList();
        stack.add(clientInstance.getstatic());
        stack.add(new MethodInsnNode(INVOKEVIRTUAL, "client", "getForcedAction", "()L" + MenuAction.class.getName().replace('.', '/') + ";", false));
        stack.add(new MethodInsnNode(INVOKEVIRTUAL, MenuAction.class.getName().replace('.', '/'), function, "()I", false));
        stack.add(new VarInsnNode(ISTORE, local));
        return stack;
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
