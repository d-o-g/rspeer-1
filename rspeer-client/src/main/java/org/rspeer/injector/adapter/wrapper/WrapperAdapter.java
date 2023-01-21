package org.rspeer.injector.adapter.wrapper;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;

import java.util.Map;

public final class WrapperAdapter extends CodeAdapter {

    private final InjectedWrapper wrapper;
    private final String constructorType;

    public WrapperAdapter(Modscript modscript, Map<String, ClassNode> library, InjectedWrapper wrapper, String constructorType) {
        super(modscript, library);
        this.wrapper = wrapper;
        this.constructorType = constructorType;
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        cn.fields.add(new FieldNode(ACC_PRIVATE, "wrapper", "L" + wrapper.getAdapterName() + ";", null, null));
        MethodNode getter = new MethodNode(ACC_PUBLIC, "getWrapper", "()" + "L" + wrapper.getAdapterName() + ";", null, null);
        getter.instructions.add(new VarInsnNode(ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(GETFIELD, cn.name, "wrapper", "L" + wrapper.getAdapterName() + ";"));
        getter.instructions.add(new InsnNode(ARETURN));
        cn.methods.add(getter);

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("<init>")) {
                InsnList stack = new InsnList();
                stack.add(new VarInsnNode(ALOAD, 0));
                stack.add(new TypeInsnNode(NEW, wrapper.getAdapterName()));
                stack.add(new InsnNode(DUP));
                stack.add(new VarInsnNode(ALOAD, 0));
                stack.add(new MethodInsnNode(INVOKESPECIAL, wrapper.getAdapterName(), "<init>",
                        "(L" + (constructorType != null ? constructorType : wrapper.getPeerName()) + ";)V", false));
                stack.add(new FieldInsnNode(PUTFIELD, cn.name, "wrapper", "L" + wrapper.getAdapterName() + ";"));
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == RETURN) {
                        mn.instructions.insertBefore(ain, stack);
                    }
                }
            }
        }
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }
}
