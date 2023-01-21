package org.rspeer.injector.adapter.callback;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.api.CodeGenerator;
import org.rspeer.injector.hook.FieldHook;

import java.util.Map;

/**
 * Created by Zachary Herridge on 2/7/2018.
 */
public final class MapRegionCallback extends MediatorDelegate<FieldHook> {

    public MapRegionCallback(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, modscript.classes.get("Client").getField("mapRegions"), library);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain != null && ain.getOpcode() == GETSTATIC && hook.match((FieldInsnNode) ain) && ain.getNext() != null
                    && ain.getNext().getNext() != null && ain.getNext().getNext().getNext() != null
                    && ain.getNext().getNext().getNext().getOpcode() == IASTORE) {
                InsnList stack = new CodeGenerator().append(referenceMediator())
                        .invokeVirtual(-1, MEDIATOR, "mapRegionChanged", "()V")
                        .collect();
                mn.instructions.insert(ain.getNext(), stack);
            }
        }
    }
}
