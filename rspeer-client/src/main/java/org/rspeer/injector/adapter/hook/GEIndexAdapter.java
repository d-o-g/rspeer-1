package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;

import java.util.Map;

public final class GEIndexAdapter extends CodeAdapter {

    private final ClassHook offer;
    private final FieldHook offers;

    public GEIndexAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
        offer = modscript.resolve(RSGrandExchangeOffer.class);
        offers = modscript.resolve(RSClient.class).getField("grandExchangeOffers");
    }

    private static AbstractInsnNode firstWithin(AbstractInsnNode src, int opcode, int dist, boolean next) {
        for (int i = 0; i < dist; i++) {
            src = next ? src.getNext() : src.getPrevious();
            if (src == null) {
                break;
            } else if (src.getOpcode() == opcode) {
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
            if (ain != null && ain.getOpcode() == GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (!offers.match(fin)) {
                    continue;
                }

                AbstractInsnNode caret = firstWithin(ain, AASTORE, 9, true);
                if (caret != null) {
                    AbstractInsnNode idx = firstWithin(ain, ILOAD, 2, true);
                    if (idx == null) {
                        continue;
                    }
                    InsnList stack = new InsnList();
                    //grandExchangeOffers[index].setIndex(index);
                    stack.add(offers.getstatic());
                    stack.add(new VarInsnNode(ILOAD, ((VarInsnNode) idx).var));
                    stack.add(new InsnNode(AALOAD));
                    stack.add(new VarInsnNode(ILOAD, ((VarInsnNode) idx).var));
                    stack.add(new MethodInsnNode(INVOKEVIRTUAL, offer.getInternalName(), "setIndex", "(I)V", false));

                    mn.instructions.insert(caret, stack);
                }
            }
        }
    }
}
