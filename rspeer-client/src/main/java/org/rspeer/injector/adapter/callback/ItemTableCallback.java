package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;

/**
 * Created by Spencer on 27/01/2018.
 */
public final class ItemTableCallback extends MediatorDelegate<FieldHook> {

    public ItemTableCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getField("itemTables"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if (!mn.desc.endsWith("V") || !mn.desc.startsWith("(IIII")) {
            return;
        }
        boolean inject = false;
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (hook.match(fin)) {
                    inject = true;
                    break;
                }
            }
        }
        if (!inject) {
            return;
        }
        InsnList stack = new CodeGenerator().append(referenceMediator())
                .loadLocal(ILOAD, 0)
                .loadLocal(ILOAD, 1)
                .loadLocal(ILOAD, 2)
                .loadLocal(ILOAD, 3)
                .invokeVirtual(-1, MEDIATOR, "itemTableChange", "(IIII)V")
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
    }
}
