package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Spencer on 24/02/2018.
 */
public final class ComponentMenuOpcodesAdapter extends CodeAdapter {

    public ComponentMenuOpcodesAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        List<AbstractInsnNode> remove = new ArrayList<>();
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain instanceof IntInsnNode) {
                IntInsnNode num = (IntInsnNode) ain;
                if (num.operand == 1006 && ain.getPrevious() != null && ain.getPrevious().getOpcode() == ICONST_0
                        && ain.getPrevious().getPrevious() != null
                        && ain.getPrevious().getPrevious().getOpcode() == GETSTATIC) {
                    remove.add(ain.getNext());
                    remove.add(ain);
                    remove.add(ain.getPrevious());
                    remove.add(ain.getPrevious().getPrevious());
                }
            }
        }

        for (AbstractInsnNode rmv : remove) {
            mn.instructions.remove(rmv);
        }
    }
}
