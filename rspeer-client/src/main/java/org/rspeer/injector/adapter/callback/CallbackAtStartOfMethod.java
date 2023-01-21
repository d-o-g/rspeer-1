package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Predicate;

/**
 * "Lazy" method of injecting callbacks, always injects at the start of the method.
 */
public final class CallbackAtStartOfMethod extends MediatorDelegate<MethodHook> {

    private final String call;

    public CallbackAtStartOfMethod(Modscript modscript, Map<String, ClassNode> library, MethodHook hook, String call) {
        super(modscript, hook, library);
        this.call = call;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        StringBuilder desc = new StringBuilder();
        desc.append('(');

        Type returnType = Type.getReturnType(mn.desc);
        InsnList stack = referenceMediator();

        if (!hook.getExpectedDesc().equals("omit")) {
            Type[] paramTypes = Type.getArgumentTypes(hook.getExpectedDesc());
            int start = 0;

            if ((mn.access & ACC_STATIC) == 0) {
                ClassHook selfHook = modscript.classes.get(hook.getOwner());
                desc.append('L').append(PROVIDER_PACKAGE).append("RS").append(selfHook.getDefinedName()).append(';');
                stack.add(new VarInsnNode(ALOAD, 0));
                start++;
            }

            for (Type type : paramTypes) {
                stack.add(new VarInsnNode(type.getOpcode(ILOAD), start++));
                desc.append(resolveDescFor(type));
            }
        }
        desc.append(')');
        desc.append(resolveDescFor(returnType));

        stack.add(new MethodInsnNode(INVOKEVIRTUAL, MEDIATOR, call, desc.toString(), false));

        mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
    }

    private String resolveDescFor(Type type) {
        String typeDesc = type.getDescriptor();
        ClassHook hook = modscript.classes.get(type.getClassName());
        if (hook != null) {
            typeDesc = typeDesc.replace(type.getClassName(), PROVIDER_PACKAGE + "RS" + hook.getDefinedName());
        }
        return typeDesc;
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return c -> hook != null && c.name.equals(hook.getOwner());
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> m.name.equals(hook.getInternalName()) && m.desc.equals(hook.getDesc());
    }
}
