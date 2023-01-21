package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.ui.Log;

import java.util.Map;

public final class AntibanAdapter extends CodeAdapter {

    public AntibanAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if ((mn.access & ACC_PUBLIC) > 0
                || (mn.access & ACC_FINAL) == 0
                || (mn.access & ACC_PRIVATE) > 0
                || !mn.desc.endsWith("V")) {
            return;
        }

        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == LDC) {
                LdcInsnNode ldc = (LdcInsnNode) ain;
                if (ldc.cst.equals(50L)) {
                    AbstractInsnNode store = ldc;
                    for (int i = 0; i < 15; i++) {
                        if (store.getOpcode() == LSTORE) {
                            break;
                        }
                        store = store.getNext();
                    }

                    mn.instructions.insertBefore(store, new InsnNode(POP2));
                    mn.instructions.insertBefore(store, new LdcInsnNode(-1L));
                    Log.fine("Weed");
                }
            }
        }
    }
}
