package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.adapter.HookAdapter;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Hook;

import java.util.Map;

public abstract class MediatorDelegate<T extends Hook> extends HookAdapter<T> {

    public static final String MEDIATOR = EVENT_PACKAGE + "EventMediator";
    public static final String MEDIATOR_OBJECT = "L" + MEDIATOR + ";";

    private final FieldHook clientInstance;

    public MediatorDelegate(CodeAdapter delegate, Modscript modscript, T hook, Map<String, ClassNode> library) {
        super(delegate, modscript, hook, library);
        clientInstance = modscript.classes.get("Client").getField("instance");
    }

    public MediatorDelegate(Modscript modscript, T hook, Map<String, ClassNode> library) {
        this(null, modscript, hook, library);
    }

    //create instruction set to invoke client.instance.getEventMediator()
    protected InsnList referenceMediator() {
        return new CodeGenerator().loadStaticField(clientInstance.getOwner(), clientInstance.getInternalName(), clientInstance.getDesc())
                .invokeVirtual(-1, "client", "getEventMediator", "()" + MEDIATOR_OBJECT)
                .collect();
    }
}
