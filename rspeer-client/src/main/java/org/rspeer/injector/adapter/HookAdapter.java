package org.rspeer.injector.adapter;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.hook.Hook;

import java.util.Map;

public abstract class HookAdapter<T extends Hook> extends CodeAdapter implements Opcodes {

    protected T hook;

    public HookAdapter(CodeAdapter delegate, Modscript modscript, T hook, Map<String, ClassNode> library) {
        super(delegate, modscript, library);
        this.hook = hook;
    }

    public HookAdapter(Modscript modscript, T hook, Map<String, ClassNode> library) {
        this(null, modscript, hook, library);
    }

    public boolean runnable() {
        return true;
    }
}
