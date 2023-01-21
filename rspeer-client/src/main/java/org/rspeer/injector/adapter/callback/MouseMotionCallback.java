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
public final class MouseMotionCallback extends MediatorDelegate<FieldHook> {

    public MouseMotionCallback(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library) {
        super(delegate, modscript, null, library);
    }

    public MouseMotionCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, null, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        InsnList instructions = new CodeGenerator().append(referenceMediator())
                .loadLocal(ALOAD, 1)
                .invokeVirtual(-1, MEDIATOR, "mouseEvent", "(Ljava/awt/event/MouseEvent;)V")
                .collect();
        mn.instructions.insertBefore(mn.instructions.getFirst(), instructions);
    }

    @Override
    public Predicate<MethodNode> methodPredicate() {
        return m -> m.name.equals("mouseMoved") || m.name.equals("mouseDragged")
                || m.name.equals("mouseClicked") || m.name.equals("mousePressed")
                || m.name.equals("mouseReleased") || m.name.equals("mouseEntered")
                || m.name.equals("mouseExited");
    }
}
