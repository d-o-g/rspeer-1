package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.api.InsnIterator;
import org.rspeer.runetek.providers.RSConnectionContext;
import org.rspeer.runetek.providers.RSOutgoingPacket;

import java.util.Map;
import java.util.function.Predicate;

public final class MouseActionSnapshotAdapter extends MediatorDelegate {


    private final String packetDesc;

    public MouseActionSnapshotAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
        this.packetDesc = "L" + modscript.resolve(RSOutgoingPacket.class).getInternalName() + ";";
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {

            if (ain.getOpcode() != LSTORE) {
                continue;
            }

            AbstractInsnNode target = ain.getPrevious();
            if (target == null || target.getOpcode() != LDIV) {
                continue;
            }

            // EVEN THIS MIGHT BE TOO MUCH
            AbstractInsnNode prev = target.getPrevious();
            if (prev == null || prev.getOpcode() != LDC) {
                continue;
            }

            LdcInsnNode ldc = (LdcInsnNode) prev;
            if (!(ldc.cst instanceof Long) || (Long) ldc.cst != 50) {
                continue;
            }

            AbstractInsnNode dest = findExprStart(ain);
            if (dest == null) {
                continue;
            }

            InsnList instructions = new CodeGenerator().append(referenceMediator())
                    .invokeVirtual(-1, MEDIATOR, "notifyMouseActionSnapshot", "()V")
                    .collect();
            mn.instructions.insertBefore(dest, instructions);

            InsnIterator insnIterator = new InsnIterator(ain);
            MethodInsnNode call = insnIterator.nextOf(MethodInsnNode.class,
                    m -> m.getOpcode() == INVOKEVIRTUAL && m.desc.contains(packetDesc)
            );

            instructions = new CodeGenerator().append(referenceMediator())
                    .invokeVirtual(-1, MEDIATOR, "notifyMouseActionPacketSent", "()V")
                    .collect();
            mn.instructions.insert(call, instructions);

            break;
        }
    }

    private AbstractInsnNode findExprStart(AbstractInsnNode ain) {
        boolean flag = false;
        AbstractInsnNode dest = ain;
        for (int i = 0; i < 20 && (dest = dest.getPrevious()) != null; i++) {
            if (dest.getOpcode() == GETSTATIC) {
                if (flag) {
                    break;
                }

                flag = true;
            }
        }

        if (!flag || dest == null) {
            return null;
        }

        if (dest.getPrevious() != null && dest.getPrevious().getOpcode() == LMUL) {
            dest = dest.getPrevious();
        }

        return dest;
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals("client");
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.desc.endsWith("V");
    }
}
