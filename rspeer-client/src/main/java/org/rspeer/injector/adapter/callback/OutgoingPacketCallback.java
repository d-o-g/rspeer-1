package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.providers.RSConnectionContext;
import org.rspeer.runetek.providers.RSOutgoingPacket;

import java.util.Map;
import java.util.function.Predicate;

public final class OutgoingPacketCallback extends MediatorDelegate<FieldHook> {

    public OutgoingPacketCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        /*for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == RETURN) {
                InsnList instructions = new CodeGenerator().append(referenceMediator())
                        .loadLocal(ALOAD, 1)
                        .invokeVirtual(-1, MEDIATOR, "writePacket", "(L" + PROVIDER_PACKAGE + "RSOutgoingPacket;)V")
                        .collect();
                System.out.println("Injected " + mn.name + mn.desc);
                mn.instructions.insertBefore(ain, instructions);
            }
        }*/

        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ALOAD, 1)
                .invokeVirtual(-1, MEDIATOR, "writePacket", "(L" + PROVIDER_PACKAGE + "RSOutgoingPacket;)V")
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return c -> c.name.equals(modscript.resolve(RSConnectionContext.class).getInternalName());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        String def = modscript.resolve(RSOutgoingPacket.class).getInternalName();
        return m -> m.desc.startsWith("(L" + def + ";") && m.desc.endsWith("V")
                && Type.getArgumentTypes(m.desc).length <= 2;
    }
}
