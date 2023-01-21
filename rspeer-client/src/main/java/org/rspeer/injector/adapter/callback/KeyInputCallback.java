package org.rspeer.injector.adapter.callback;

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
public final class KeyInputCallback extends MediatorDelegate<FieldHook> {

    public KeyInputCallback(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library) {
        super(delegate, modscript, null, library);
    }

    public KeyInputCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ALOAD, 1)
                .invokeVirtual(-1, MEDIATOR, "keyEvent", "(Ljava/awt/event/KeyEvent;)V")
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> m.name.equals("keyTyped") || m.name.equals("keyPressed")
                || m.name.equals("keyReleased");
    }
}
