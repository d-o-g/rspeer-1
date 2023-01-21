package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Spencer on 05/07/2018.
 */
public final class ObjectFloorLevelAdapter extends HookAdapter<MethodHook> {

    private final String type;

    public ObjectFloorLevelAdapter(Modscript modscript, String type, MethodHook hook, Map<String, ClassNode> library) {
        super(modscript, hook, library);
        this.type = type;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == ASTORE && ain.getPrevious().getOpcode() == INVOKESPECIAL) {
                MethodInsnNode min = (MethodInsnNode) ain.getPrevious();
                VarInsnNode store = (VarInsnNode) ain;
                if (min.name.equals("<init>") && min.owner.equals(type)) {
                    InsnList stack = new InsnList();
                    stack.add(new VarInsnNode(ALOAD, store.var));
                    stack.add(new VarInsnNode(ILOAD, 1)); //find floorLevel var
                    stack.add(new FieldInsnNode(PUTFIELD, type, "floorLevel", "I"));
                    mn.instructions.insert(ain, stack);
                }
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return x -> x.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return x -> x.name.equals(hook.getInternalName()) && x.desc.equals(hook.getDesc());
    }
}
