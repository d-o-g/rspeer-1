package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

public final class MessageCallback extends MediatorDelegate<MethodHook> {

    private static final String MSG_RECV_DESC = "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    public MessageCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getMethod("messageReceived"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    //static void addMessage(int type, String source, String message, String channel)
    @Override
    public void visitMethodNode(MethodNode mn) {
        //for this callback we can just inject at the start of the method
        //references the EventMediator and then calls messageReceived with the first 4 args
        //equivalent to client.instance.getEventMediator().messageReceived(var0, var1, var2, var3)
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ILOAD, 0)
                .loadLocal(ALOAD, 1)
                .loadLocal(ALOAD, 2)
                .loadLocal(ALOAD, 3)
                .invokeVirtual(-1, MEDIATOR, "messageReceived", MSG_RECV_DESC)
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
