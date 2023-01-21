package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.providers.RSAsyncConnection;

import java.util.Map;
import java.util.function.Predicate;

public final class SocketAdapter extends MediatorDelegate {

    private final ClassHook connection;

    public SocketAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
        connection = modscript.resolve(RSAsyncConnection.class);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain.getOpcode() == PUTFIELD) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                AbstractInsnNode value = fin.getPrevious();
                if (!fin.desc.contains("Socket") || value == null || value.getOpcode() != ALOAD) {
                    continue;
                }
                InsnList stack = new InsnList();
                stack.add(referenceMediator());
                stack.add(new VarInsnNode(ALOAD, ((VarInsnNode) value).var));
                stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, "getSocket", "(Ljava/net/Socket;)Ljava/net/Socket;", false));
                mn.instructions.insertBefore(ain, stack);
                mn.instructions.remove(value);
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(connection.getInternalName());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.name.equals("<init>");
    }
}
