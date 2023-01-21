package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Spencer on 23/08/2018.
 */
public final class ExceptionSuppressorAdapter extends CodeAdapter {

    public ExceptionSuppressorAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
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
        stack.add(new JumpInsnNode(IFNE, ln));
        stack.add(new VarInsnNode(ALOAD, 1));
        stack.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false));
        stack.add(new InsnNode(RETURN));
        stack.add(ln);

        mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
    }

    public Predicate<MethodNode> methodPredicate() {
        return x -> x.desc.startsWith("(Ljava/lang/String;Ljava/lang/Throwable;") && (x.access & ACC_STATIC) > 0;
    }
}
