package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.commons.BotPreferences;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;

import java.util.Map;
import java.util.function.Predicate;

public final class ChristmasModeAdapter extends CodeAdapter {

    private static final boolean ENABLED = false;// BotPreferences.getInstance().isFestiveMode();

    public ChristmasModeAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
    }

    private static boolean findConstant(MethodNode mn, Object cst) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == LDC) {
                LdcInsnNode ldc = (LdcInsnNode) ain;
                if (ldc.cst != null && ldc.cst.equals(cst)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if (!ENABLED) {
            return;
        }

        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() != IF_ICMPLE) {
                continue;
            }

            AbstractInsnNode tmp = ain;
            for (int i = 0; i < 6 && tmp != null; i++) {
                if (tmp.getOpcode() == SIPUSH) {
                    IntInsnNode iin = (IntInsnNode) tmp;
                    if (iin.operand == 255) {
                        mn.instructions.set(iin, new IntInsnNode(SIPUSH, -255));
                    }
                }
                tmp = tmp.getPrevious();
            }
        }
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> ENABLED && findConstant(m, 512.0D);
    }
}
