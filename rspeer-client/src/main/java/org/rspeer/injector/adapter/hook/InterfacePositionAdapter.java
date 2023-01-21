package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.providers.RSInterfaceComponent;

import java.util.Map;

public final class InterfacePositionAdapter extends CodeAdapter {

    private final FieldHook relY;

    public InterfacePositionAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
        relY = modscript.resolve(RSInterfaceComponent.class).getField("relativeY");
    }

    private static AbstractInsnNode checkSurrounding(AbstractInsnNode src, int dist, int op) {
        AbstractInsnNode curr = src;
        for (int i = 0; i < dist && curr != null; i++) {
            if (curr.getOpcode() == op) {
                return curr;
            }
            curr = curr.getNext();
        }
        curr = src;
        for (int i = 0; i < dist && curr != null; i++) {
            if (curr.getOpcode() == op) {
                return curr;
            }
            curr = curr.getPrevious();
        }
        return null;
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) != 0 && mn.desc.startsWith("([L" + relY.getOwner() + ";IIIIIII")) {
                outer:
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() != IADD || ain.getNext() == null || ain.getNext().getOpcode() != ISTORE) {
                        continue;
                    }
                    int aload = -1, iload = -1;
                    AbstractInsnNode tmp = ain;
                    inner:
                    for (int i = 0; i < 10 && tmp != null; i++) {
                        if (tmp.getOpcode() == GETFIELD) {
                            FieldInsnNode k = (FieldInsnNode) tmp;
                            if (k.owner.equals(relY.getOwner()) && k.name.equals(relY.getInternalName())) {
                                AbstractInsnNode chk = checkSurrounding(tmp, 6, ILOAD);
                                if (chk == null) {
                                    continue outer;
                                }
                                iload = ((VarInsnNode) chk).var;
                                AbstractInsnNode tmp2 = tmp;
                                for (int j = 0; j < 5 && tmp2 != null; j++) {
                                    if (tmp2.getOpcode() == ALOAD) {
                                        aload = ((VarInsnNode) tmp2).var;
                                        break inner;
                                    }
                                    tmp2 = tmp2.getPrevious();
                                }
                            }
                        }
                        tmp = tmp.getPrevious();
                    }
                    if (aload == -1 || iload == -1) {
                        continue;
                    }
                    InsnList setStack = new InsnList();
                    setStack.add(new VarInsnNode(ALOAD, aload));
                    setStack.add(new VarInsnNode(ILOAD, iload - 1));
                    setStack.add(new MethodInsnNode(INVOKEVIRTUAL, relY.getOwner(), "setRootX", "(I)V", false));
                    setStack.add(new VarInsnNode(ALOAD, aload));
                    setStack.add(new VarInsnNode(ILOAD, iload));
                    setStack.add(new MethodInsnNode(INVOKEVIRTUAL, relY.getOwner(), "setRootY", "(I)V", false));
                    mn.instructions.insert(ain.getNext(), setStack);
                    break;
                }
            }
        }
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }
}
