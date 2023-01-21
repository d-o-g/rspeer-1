package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;

public final class VarpCallback extends MediatorDelegate<FieldHook> {

    public VarpCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getField("varps"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain == null || ain.getOpcode() != Opcodes.GETSTATIC || !hook.match((FieldInsnNode) ain)
                    || ain.getNext() == null || ain.getNext().getNext() == null
                    || ain.getNext().getNext().getNext() == null
                    || ain.getNext().getNext().getNext().getOpcode() != Opcodes.IASTORE) {
                continue;
            }
            AbstractInsnNode val = ain.getNext().getNext();
            int index = ((VarInsnNode) ain.getNext()).var;
            InsnList stack = new CodeGenerator().append(referenceMediator())
                    .loadLocal(Opcodes.ILOAD, index)
                    .loadStaticField(hook.getOwner(), hook.getInternalName(), hook.getDesc())
                    .loadLocal(Opcodes.ILOAD, index)
                    .append(new InsnNode(Opcodes.IALOAD))
                    .collect();
            if (val.getOpcode() == Opcodes.ICONST_0) {
                stack.add(new InsnNode(Opcodes.ICONST_0));
            } else if (val.getOpcode() == Opcodes.ILOAD) {
                stack.add(new VarInsnNode(Opcodes.ILOAD, ((VarInsnNode) val).var));
            } else {
                continue;
            }
            stack.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MEDIATOR, "varpChanged", "(III)V", false));
            mn.instructions.insertBefore(val, stack);
        }
    }
}
