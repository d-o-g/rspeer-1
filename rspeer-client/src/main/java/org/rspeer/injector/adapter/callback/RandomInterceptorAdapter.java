package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.providers.RSClient;

import java.util.Map;

public final class RandomInterceptorAdapter extends MediatorDelegate {

    private final FieldHook random;

    public RandomInterceptorAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
        random = modscript.resolve(RSClient.class).getField("random");
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (!random.match(fin)) {
                    continue;
                }

                InsnList stack = new InsnList();
                stack.add(referenceMediator());
                stack.add(new FieldInsnNode(GETSTATIC, fin.owner, fin.name, fin.desc));
                stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, "getRandom", "([B)[B", false));

                mn.instructions.insertBefore(ain, stack);
                mn.instructions.remove(ain);
            }
        }
    }
}
