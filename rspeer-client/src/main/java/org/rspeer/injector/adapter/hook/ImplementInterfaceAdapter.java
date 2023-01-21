package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.hook.ClassHook;

import java.util.Map;

public final class ImplementInterfaceAdapter extends HookAdapter<ClassHook> {

    public ImplementInterfaceAdapter(Modscript modscript, ClassHook hook, Map<String, ClassNode> library) {
        super(modscript, hook, library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        cn.interfaces.add(HookAdapter.PROVIDER_PACKAGE + "RS" + hook.getDefinedName());
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }
}
