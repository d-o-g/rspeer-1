package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.runetek.event.EventMediator;

import java.util.Map;
import java.util.function.Predicate;

public final class ProcessActionCallback extends MediatorDelegate<MethodHook> {

    private static final String PROCESS_ACTION_DESC = "(IIIILjava/lang/String;Ljava/lang/String;II)V";

    public ProcessActionCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("processAction"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(Opcodes.ILOAD, 0)
                .loadLocal(Opcodes.ILOAD, 1)
                .loadLocal(Opcodes.ILOAD, 2)
                .loadLocal(Opcodes.ILOAD, 3)
                .loadLocal(Opcodes.ALOAD, 4)
                .loadLocal(Opcodes.ALOAD, 5)
                .loadLocal(Opcodes.ILOAD, 6)
                .loadLocal(Opcodes.ILOAD, 7)
                .invokeVirtual(-1, MEDIATOR, "actionProcessed", PROCESS_ACTION_DESC)
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);

        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == RETURN) {
                instructions = new CodeGenerator().append(referenceMediator())
                        .loadLocal(Opcodes.ILOAD, 0)
                        .loadLocal(Opcodes.ILOAD, 1)
                        .loadLocal(Opcodes.ILOAD, 2)
                        .loadLocal(Opcodes.ILOAD, 3)
                        .loadLocal(Opcodes.ALOAD, 4)
                        .loadLocal(Opcodes.ALOAD, 5)
                        .loadLocal(Opcodes.ILOAD, 6)
                        .loadLocal(Opcodes.ILOAD, 7)
                        .invokeVirtual(-1, MEDIATOR, "postActionProcessed", PROCESS_ACTION_DESC)
                        .collect();
                mn.instructions.insertBefore(ain, instructions);
            }
        }

        //cuck
        InsnList filter = new InsnList();
        Label label = new Label();
        LabelNode ln = new LabelNode(label);
        mn.visitLabel(label);
        filter.add(referenceMediator());
        filter.add(new VarInsnNode(ILOAD, 0));
        filter.add(new VarInsnNode(ILOAD, 1));
        filter.add(new VarInsnNode(ILOAD, 2));
        filter.add(new VarInsnNode(ILOAD, 3));
        filter.add(new VarInsnNode(ALOAD, 4));
        filter.add(new VarInsnNode(ALOAD, 5));
        filter.add(new VarInsnNode(ILOAD, 6));
        filter.add(new VarInsnNode(ILOAD, 7));
        filter.add(new MethodInsnNode(INVOKEVIRTUAL, EventMediator.class.getName().replace('.', '/'), "filterAction", PROCESS_ACTION_DESC.replace("V", "Z"), false));
        filter.add(new JumpInsnNode(IFEQ, ln));
        filter.add(new InsnNode(RETURN));
        filter.add(ln);

        mn.instructions.insertBefore(mn.instructions.getFirst(), filter);
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
