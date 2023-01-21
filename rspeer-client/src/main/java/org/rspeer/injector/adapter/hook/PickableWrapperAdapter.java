package org.rspeer.injector.adapter.hook;


import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSPickable;

import java.util.Map;
import java.util.function.Predicate;

public final class PickableWrapperAdapter extends HookAdapter<FieldHook> {

    private final ClassHook pickable;
    private final Predicate<AbstractInsnNode> level;

    public PickableWrapperAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSClient.class).getField("pickableNodeDeques"), library);
        pickable = modscript.resolve(RSPickable.class);
        FieldHook level = modscript.resolve(RSClient.class).getField("floorLevel");
        this.level = x -> x instanceof FieldInsnNode && ((FieldInsnNode) x).owner.equals(level.getOwner())
                && ((FieldInsnNode) x).name.equals(level.getInternalName());
    }

    private static AbstractInsnNode firstWithin(AbstractInsnNode src,
                                                Predicate<AbstractInsnNode> predicate,
                                                int dist, boolean next) {
        for (int i = 0; i < dist; i++) {
            src = next ? src.getNext() : src.getPrevious();
            if (src == null) {
                break;
            } else if (predicate.test(src)) {
                return src;
            }
        }
        return null;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (hook.match(fin)) {
                    AbstractInsnNode rx = firstWithin(fin, level, 5, true);
                    if (rx != null && (rx = firstWithin(fin,  x -> x.getOpcode() == ILOAD, 5, true)) != null) {
                        VarInsnNode ry = (VarInsnNode) firstWithin(rx, x -> x.getOpcode() == ILOAD, 5, true);
                        AbstractInsnNode invoke = firstWithin(rx, x -> x.getOpcode() == INVOKEVIRTUAL, 7, true);
                        if (ry != null && invoke != null) {
                            VarInsnNode pickable = (VarInsnNode) firstWithin(invoke, x -> x.getOpcode() == ALOAD, 4, false);
                            if (pickable == null) {
                                continue;
                            }
                            String adapter = Pickable.class.getName().replace('.', '/');
                            String peer = RSPickable.class.getName().replace('.', '/');
                            String scene = Scene.class.getName().replace('.', '/');


                            InsnList stack = new InsnList();
                            stack.add(new VarInsnNode(ALOAD, pickable.var));
                            stack.add(new TypeInsnNode(NEW, adapter));
                            stack.add(new InsnNode(DUP));
                            stack.add(new VarInsnNode(ALOAD, pickable.var));
                            stack.add(new VarInsnNode(ILOAD, ry.var - 1));
                            stack.add(new VarInsnNode(ILOAD, ry.var));
                            stack.add(new MethodInsnNode(INVOKESTATIC, scene, "getFloorLevel", "()I", false));
                            stack.add(new MethodInsnNode(INVOKESPECIAL, adapter, "<init>", "(L" + peer + ";III)V", false));
                            stack.add(new FieldInsnNode(PUTFIELD, this.pickable.getInternalName(), "wrapper", "L" + adapter + ";"));
                            mn.instructions.insert(invoke, stack);
                        }
                    }
                }
            }
        }
    }
}
