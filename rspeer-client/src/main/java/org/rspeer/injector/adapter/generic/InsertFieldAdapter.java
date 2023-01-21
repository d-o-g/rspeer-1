package org.rspeer.injector.adapter.generic;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;

import java.util.Map;
import java.util.function.Predicate;

public final class InsertFieldAdapter extends CodeAdapter {

    /**
     * If this is true, injects a getter and setter
     **/
    private final boolean accessible;
    private final FieldMeta meta;

    public InsertFieldAdapter(Modscript modscript, Map<String, ClassNode> library, FieldMeta meta, boolean accessible) {
        super(new GetterAdapter(new SetterAdapter(modscript, library, meta) {
            @Override
            public boolean runnable() {
                return accessible;
            }
        }, modscript, library, meta) {
            @Override
            public boolean runnable() {
                return accessible;
            }
        }, modscript, library);
        this.accessible = accessible;
        this.meta = meta;
    }

    public InsertFieldAdapter(Modscript modscript, Map<String, ClassNode> library, FieldMeta meta) {
        this(modscript, library, meta, false);
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        cn.fields.add(new FieldNode(ACC_PUBLIC, meta.name, meta.desc, null, null));
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }

    @Override
    public Predicate<ClassNode> classPredicate() {
        return cn -> cn.name.equals(meta.owner);
    }
}
