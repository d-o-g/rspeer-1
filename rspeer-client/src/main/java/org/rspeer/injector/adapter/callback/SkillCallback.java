package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.event.types.SkillEvent;

import java.util.Map;

public final class SkillCallback extends MediatorDelegate<FieldHook> {

    private final int type;

    public SkillCallback(Modscript modscript, Map<String, ClassNode> library, int type) {
        super(modscript, modscript.classes.get("Client").getField(typeToName(type)), library);
        this.type = type;
    }

    private static String typeToName(int type) {
        if (type == SkillEvent.TYPE_EXPERIENCE) {
            return "experiences";
        } else if (type == SkillEvent.TYPE_LEVEL) {
            return "levels";
        } else if (type == SkillEvent.TYPE_TEMPORARY_LEVEL) {
            return "currentLevels";
        }
        throw new IllegalArgumentException("Unknown type");
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == GETSTATIC) {
                FieldInsnNode ref = (FieldInsnNode) ain;
                if (!ref.owner.equals(hook.getOwner()) || !ref.name.equals(hook.getInternalName())) {
                    continue;
                }
                if (ref.getNext() != null && ref.getNext().getNext() != null
                        && ref.getNext() instanceof VarInsnNode
                        && ref.getNext().getNext() instanceof VarInsnNode
                        && ref.getNext().getNext().getNext() != null
                        && ref.getNext().getNext().getNext().getOpcode() == IASTORE) {
                    VarInsnNode valueref = (VarInsnNode) ref.getNext();
                    VarInsnNode indexref = (VarInsnNode) ref.getNext().getNext();
                    InsnList stack = new CodeGenerator().append(referenceMediator())
                            .loadLocal(ILOAD, valueref.var)
                            .loadLocal(ILOAD, indexref.var)
                            .loadConstant(type)
                            .invokeVirtual(-1, MEDIATOR, "notifySkillUpdate", "(III)V")
                            .collect();
                    mn.instructions.insertBefore(ref, stack);
                    //insert before the arraystore ^
                }
            }
        }
    }
}
