package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.api.TypeHelper;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.MethodHook;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MethodHookInvokerAdapter extends HookAdapter<MethodHook> {

    public MethodHookInvokerAdapter(Modscript modscript, MethodHook hook, Map<String, ClassNode> library) {
        super(modscript, hook, library);
    }

    //a function which takes an array of types and adds XLOAD instructions for each type
    private static Function<Type[], Function<CodeGenerator, CodeGenerator>> loadArgs(Modscript script) {
        return types -> gen -> {
            for (int i = 0; i < types.length; i++) { //invokers are nonstatic, index 0 is reserved for 'this' so pass i + 1
                gen.append(new VarInsnNode(types[i].getOpcode(Opcodes.ILOAD), i + 1));
                String type = types[i].getDescriptor();
                if (type.contains("rspeer")) {
                    type = type.replace(";", "");
                    type = type.substring(type.lastIndexOf('/') + 1);
                    type = type.replace("RS", "");
                    ClassHook ch = script.classes.get(type);
                    if (ch != null) {
                        gen.append(new TypeInsnNode(Opcodes.CHECKCAST, ch.getInternalName()));
                    }
                }
            }
            return gen;
        };
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        MethodNode mn = createInvoker(TypeHelper.toKnownMethodDesc(modscript, hook.getExpectedDesc()));
        cn.methods.add(mn);
    }

    @Override
    public void visitMethodNode(MethodNode mn) {
    }

    private MethodNode createInvoker(String transformedDesc) {
        return new CodeGenerator()
                .appendIf(hook::isLocal, gen -> gen.loadLocal(Opcodes.ALOAD, 0))
                .append(loadArgs(modscript).apply(Type.getArgumentTypes(transformedDesc)))
                .appendIf(hook::hasPredicate, gen -> gen.loadConstant(hook.getPredicate()))
                .invokeMethod(-1, hook.isStatic(), hook.getOwner(), hook.getInternalName(), hook.getDesc())
                .returning(Type.getReturnType(transformedDesc).getOpcode(Opcodes.IRETURN))
                .createPublicMethod(hook.getDefinedName(), transformedDesc);
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> hook.isStatic() && cn.name.equals("client") || cn.name.equals(hook.getOwner());
    }
}
