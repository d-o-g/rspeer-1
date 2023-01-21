package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSVarpbit;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

public final class VarpbitAdapter extends HookAdapter<MethodHook> {

    public VarpbitAdapter(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library) {
        super(delegate, modscript, modscript.resolve(RSClient.class).getMethod("getVarpbit"), library);
    }

    public VarpbitAdapter(Modscript modscript, Map<String, ClassNode> library) {
        this(null, modscript, library);
    }

    @Override
    public void visitClassNode(ClassNode client) {
        MethodNode varpbit = null;

        for (ClassNode cn : library.values()) {
            if (cn.name.equals(hook.getOwner())) {
                for (MethodNode mn : cn.methods) {
                    if (mn.desc.equals(hook.getDesc()) && mn.name.equals(hook.getInternalName())) {
                        varpbit = mn;
                        break;
                    }
                }
            }
        }

        if (varpbit == null) {
            throw new IllegalStateException();
        }

        InsnList modded = null;
        if (!varpbit.desc.endsWith(";")) {
            MethodNode clone = new MethodNode();
            clone.tryCatchBlocks = new ArrayList<>();
            varpbit.accept(clone);
            modded = modVarpbit(clone.instructions, modscript.resolve(RSVarpbit.class).getField("varpIndex"));
        } else { //inject regular invoker
            MethodNode test = new MethodNode(ACC_PUBLIC, "getVarpbit", "(I)L" +
                    Type.getInternalName(RSVarpbit.class) + ";", null, null);
            test.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
            if (hook.hasPredicate()) {
                test.instructions.add(new LdcInsnNode(hook.getPredicate()));
            }
            test.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, hook.getOwner(), hook.getInternalName(), hook.getDesc(), false));
            test.instructions.add(new InsnNode(Opcodes.ARETURN));
            client.methods.add(test);
        }
        if (modded == null) {
            return;
        }
        MethodNode varp0 = new MethodNode(ACC_PUBLIC | ACC_STATIC, "getVarpbit0", "(II)L" +
                Type.getInternalName(RSVarpbit.class) + ";", null, null);
        varp0.instructions = modded;

        MethodNode vb0 = new MethodNode(ACC_PUBLIC, "getVarpbit", "(I)L" + Type.getInternalName(RSVarpbit.class) + ";",
                null, null);
        vb0.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        if (hook.hasPredicate()) {
            vb0.instructions.add(new LdcInsnNode(hook.getPredicate()));
        }
        vb0.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "client", varp0.name, varp0.desc, false));
        vb0.instructions.add(new InsnNode(Opcodes.ARETURN));
        client.methods.add(vb0);
        client.methods.add(varp0);
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }

    private InsnList modVarpbit(InsnList instructions, FieldHook varpIndex) {
        InsnList dumystack = new InsnList();
        if (hook.hasPredicate()) {
            dumystack.add(new LdcInsnNode(hook.getPredicate())); //dummy
        }
        dumystack.add(new VarInsnNode(ISTORE, 2));
        instructions.insertBefore(instructions.getFirst(), dumystack);
        //find astore variable index for Varpbit variable
        //find and remove:
        int varb = -1;
        for (AbstractInsnNode ain : instructions.toArray()) {
            if (ain.getOpcode() != GETFIELD) {
                continue;
            }
            FieldInsnNode fin = (FieldInsnNode) ain;
            if (!varpIndex.getOwner().equals(fin.owner) || !varpIndex.getInternalName().equals(fin.name)) {
                continue;
            }
            VarInsnNode varpbit = (VarInsnNode) ain.getPrevious();
            varb = varpbit.var;
            break;
        }

        for (AbstractInsnNode ain : instructions.toArray()) {
            if (ain.getOpcode() == IRETURN) {
                InsnList stack = new InsnList();
                stack.add(new InsnNode(POP));
                stack.add(new VarInsnNode(ALOAD, varb));
                stack.add(new InsnNode(ARETURN));
                instructions.insertBefore(ain, stack);
                stack.remove(ain);
            }
        }
        return instructions;
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals("client");
    }
}
