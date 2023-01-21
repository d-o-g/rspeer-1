package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Spencer on 27/01/2018.
 */
public final class EngineTickCallback extends MediatorDelegate<FieldHook> {

    public EngineTickCallback(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library) {
        super(delegate, modscript, null, library);
    }

    public EngineTickCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
    }

    private static boolean backTrackInsn(AbstractInsnNode ain, int insn) {
        for (int i = 0; i < 5 && (ain = ain.getPrevious()) != null; i++) {
            if (ain.getOpcode() == insn) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if (mn.name.equals("run") && mn.desc.equals("()V")) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() == PUTSTATIC && backTrackInsn(ain, INVOKEVIRTUAL)) {
                    InsnList instructions = new CodeGenerator().append(referenceMediator())
                            .invokeVirtual(-1, MEDIATOR, "onEngineTick", "()V")
                            .collect();
                    mn.instructions.insert(ain, instructions);
                }
            }
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return c -> c.superName.contains("Applet");
    }
}
