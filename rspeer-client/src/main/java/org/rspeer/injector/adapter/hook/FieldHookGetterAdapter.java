package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.api.TypeHelper;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FieldHookGetterAdapter extends HookAdapter<FieldHook> {


    //a function that takes a FieldHook, and generates instructions in a CodeGenerator
    //consisting of an LDC with the multiplier and the MUL opcode (IMUL/LMUL)
    private static final Function<FieldHook, Function<CodeGenerator, CodeGenerator>> MULTIPLIER
            = hook -> gen -> {
        long mul = hook.getMultiplier();
        int imul = (int) hook.getMultiplier();
        return hook.getDesc().equals("J") ? gen.loadConstant(mul) : gen.loadConstant(imul);
    };

    public FieldHookGetterAdapter(CodeAdapter delegate, Modscript modscript, FieldHook hook, Map<String, ClassNode> library) {
        super(delegate, modscript, hook, library);
    }

    public FieldHookGetterAdapter(Modscript modscript, FieldHook hook, Map<String, ClassNode> library) {
        super(modscript, hook, library);
    }


    private static String normalizeName(String name, String desc) {
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
        return desc.equals("Z") ? "is" + name : "get" + name;
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        cn.methods.add(createGetter(TypeHelper.toKnownType(modscript, Type.getType(hook.getDesc()))));
    }

    private MethodNode createGetter(String returnDesc) {
        return new CodeGenerator()
                .loadField(0, hook)
                .appendIf(() -> hook.getMultiplier() != 0, MULTIPLIER.apply(hook))
                .appendIf(() -> hook.getMultiplier() != 0, new InsnNode(hook.getDesc().equals("J") ? LMUL : IMUL))
                .returning(Type.getType(returnDesc).getOpcode(IRETURN))
                .createMethod(ACC_PUBLIC, normalizeName(hook.getDefinedName(), returnDesc), "()" + returnDesc);
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> hook.isStatic() && cn.name.equals("client") || cn.name.equals(hook.getOwner());
    }
}
