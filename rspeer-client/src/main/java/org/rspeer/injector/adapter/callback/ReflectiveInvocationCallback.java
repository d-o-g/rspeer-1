package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.runetek.providers.RSClient;

import java.util.Map;
import java.util.function.Predicate;

public final class ReflectiveInvocationCallback extends MediatorDelegate<MethodHook> {

    public ReflectiveInvocationCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSClient.class).getMethod("processClassStructurePacket"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof MethodInsnNode) {
                MethodInsnNode invoke = (MethodInsnNode) ain;
                if (invoke.name.equals("invoke")) {
                    AbstractInsnNode args = ain.getPrevious();
                    AbstractInsnNode instance = ain.getPrevious().getPrevious();
                    AbstractInsnNode ref = ain.getPrevious().getPrevious().getPrevious();
                    if (args.getOpcode() == ALOAD && instance.getOpcode() == ACONST_NULL && ref.getOpcode() == ALOAD) {
                        InsnList instructions = new CodeGenerator().append(referenceMediator())
                                .loadLocal(ALOAD, ((VarInsnNode) ref).var)
                                .loadLocal(ALOAD, ((VarInsnNode) args).var)
                                .invokeVirtual(-1, MEDIATOR, "onProcessClassStructure", "(Ljava/lang/reflect/Method;[Ljava/lang/Object;)V")
                                .collect();
                        mn.instructions.insertBefore(ain.getNext(), instructions);
                    }
                }
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return c -> c.name.equals(hook.getOwner());
    }

    public Predicate<MethodNode> methodPredicate() {
        return m -> m.name.equals(hook.getInternalName()) && m.desc.equals(hook.getDesc());
    }
}
