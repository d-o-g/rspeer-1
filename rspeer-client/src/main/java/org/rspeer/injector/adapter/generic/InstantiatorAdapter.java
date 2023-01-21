package org.rspeer.injector.adapter.generic;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;

import java.util.Map;

/**
 * Created by Spencer on 29/05/2018.
 * TODO add support for constructors with args
 */
public class InstantiatorAdapter extends CodeAdapter {

    private final ClassHook target;

    public InstantiatorAdapter(Modscript modscript, Map<String, ClassNode> library, ClassHook target) {
        super(modscript, library);
        this.target = target;
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "new" + target.getDefinedName(),
                "()L" + PROVIDER_PACKAGE + "RS" + target.getDefinedName() + ";", null, null);
        mn.instructions.add(new TypeInsnNode(NEW, target.getInternalName()));
        mn.instructions.add(new InsnNode(DUP));
        mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, target.getInternalName(), "<init>", "()V", false));
        mn.instructions.add(new InsnNode(ARETURN));
        cn.methods.add(mn);
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }
}
