package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.providers.RSSceneGraph;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Predicate;

public final class SceneAdapter extends HookAdapter<ClassHook> {

    public SceneAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.resolve(RSSceneGraph.class), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        if (mn.desc.startsWith("(IIIIII") && mn.desc.endsWith("V") && Modifier.isPublic(mn.access)) {
            InsnList setStack = new InsnList();
            Label label = new Label();
            LabelNode ln = new LabelNode(label);
            mn.visitLabel(label);
            setStack.add(new InsnNode(ICONST_0));
            //if (Projection.LANDSCAPE_RENDEIRNG_ENABLED)
            setStack.add(new MethodInsnNode(INVOKESTATIC, Projection.class.getName().replace('.', '/'), "isLandscapeRenderingEnabled", "()Z", false));
            setStack.add(new JumpInsnNode(IFNE, ln));
            setStack.add(new InsnNode(RETURN));
            setStack.add(ln);
            mn.instructions.insert(setStack);
        }
    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(hook.getInternalName());
    }
}
