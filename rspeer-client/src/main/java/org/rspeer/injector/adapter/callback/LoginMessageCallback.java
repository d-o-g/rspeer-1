package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class LoginMessageCallback extends MediatorDelegate<MethodHook> {

    private static final String LOGIN_DESC = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    public LoginMessageCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("setLoginMessages"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(Opcodes.ALOAD, 0)
                .loadLocal(Opcodes.ALOAD, 1)
                .loadLocal(Opcodes.ALOAD, 2)
                .invokeVirtual(-1, MEDIATOR, "onLoginError", LOGIN_DESC)
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return mn -> mn.desc.equals(hook.getDesc()) && mn.name.equals(hook.getInternalName());
    }
}
