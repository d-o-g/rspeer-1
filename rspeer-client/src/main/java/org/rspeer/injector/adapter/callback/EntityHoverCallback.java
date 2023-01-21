package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;

public final class EntityHoverCallback extends MediatorDelegate<FieldHook> {

    private static final int DISTANCE = 5;
    private static final int[] PATTERN = {
            Opcodes.GETSTATIC,
            Opcodes.DUP,
            Opcodes.ICONST_1,
            Opcodes.ILOAD,
            Opcodes.IASTORE
    };

    public EntityHoverCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getField("onCursorUids"), library);
    }

    private static boolean match(AbstractInsnNode current, int... ops) {
        for (int op : ops) {
            for (int i = 0; i < DISTANCE; i++) {
                current = current.getNext();
                if (current == null) {
                    return false;
                }
                if (current.getOpcode() == op) {
                    break;
                }
            }
        }
        return true;
    }

    private static AbstractInsnNode next(AbstractInsnNode src, int op) {
        while ((src = src.getNext()) != null) {
            if (src.getOpcode() == op) {
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
            if (ain != null && ain.getOpcode() == Opcodes.GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (hook.match(fin) && match(fin, PATTERN)) {
                    VarInsnNode lv = (VarInsnNode) next(fin, Opcodes.ILOAD);
                    if (lv == null) {
                        continue;
                    }
                    InsnList instructions = new CodeGenerator().append(referenceMediator())
                            .loadLocal(Opcodes.ILOAD, lv.var)
                            .invokeVirtual(-1, MEDIATOR, "onEntityHover", "(I)V")
                            .collect();
                    mn.instructions.insertBefore(ain, instructions);

                    //we're also be going to be modifying it with our own uids so...
                    InsnList mod = new CodeGenerator().append(referenceMediator())
                            .invokeVirtual(-1, MEDIATOR, "updateHoveredEntities", "()V")
                            .collect();
                    mn.instructions.insert(lv.getNext(), mod);
                    return;
                }
            }
        }
    }
}
