package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.providers.RSModel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class ModelAdapter extends HookAdapter<ClassHook> {

    public ModelAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSModel.class), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        List<String> badKeys = new ArrayList<>();
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                //onCursorUids[onCursorCount++] = ...; find this to tell where not to inject
                if (ain.getOpcode() != IASTORE || !matchPrevs(ain, ILOAD, PUTSTATIC, IADD, ICONST_1, DUP, GETSTATIC, GETSTATIC)) {
                    continue;
                }
                badKeys.add(mn.name + mn.desc);
            }
        }
        for (MethodNode mn : cn.methods) {
            if (!badKeys.contains(mn.name + mn.desc) && !mn.desc.contains("[B") && !Modifier.isStatic(mn.access)) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() != IFNONNULL || !matchPrevs(ain, GETFIELD, ALOAD)) {
                        continue;
                    }
                    FieldInsnNode field = (FieldInsnNode) ain.getPrevious();
                    if (field.desc.equals("[B")) {
                        VarInsnNode aload = (VarInsnNode) ain.getPrevious().getPrevious();
                        InsnList setStack = new InsnList();
                        Label label = new Label();
                        LabelNode ln = new LabelNode(label);
                        mn.visitLabel(label);
                        setStack.add(new InsnNode(ICONST_0));
                        setStack.add(new MethodInsnNode(INVOKESTATIC, Projection.class.getName().replace('.', '/'), "isModelRenderingEnabled", "()Z", false));
                        setStack.add(new JumpInsnNode(IFNE, ln));
                        setStack.add(new InsnNode(RETURN));
                        setStack.add(ln);
                        mn.instructions.insertBefore(aload, setStack);
                    }
                }
            }
        }
    }

    private boolean matchPrevs(AbstractInsnNode ain, int... ops) {
        AbstractInsnNode curr = ain;
        for (int i = 0; i < ops.length && (curr = curr.getPrevious()) != null; i++) {
            if (curr.getOpcode() != ops[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getInternalName());
    }
}
