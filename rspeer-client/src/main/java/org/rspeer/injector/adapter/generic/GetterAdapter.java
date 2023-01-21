package org.rspeer.injector.adapter.generic;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.api.CodeGenerator;

import java.util.Map;

public class GetterAdapter extends CodeAdapter {

    private final FieldMeta meta;

    public GetterAdapter(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library, FieldMeta meta) {
        super(delegate, modscript, library);
        this.meta = meta;
    }

    public GetterAdapter(Modscript modscript, Map<String, ClassNode> library, FieldMeta meta) {
        this(null, modscript, library, meta);
    }

    @Override
    public void visitClassNode(ClassNode cn) {
        MethodNode mn = new CodeGenerator()
                .loadField(0, meta.isStatic, meta.owner, meta.name, meta.desc)
                .returning(Type.getType(meta.desc).getOpcode(IRETURN))
                .createMethod(ACC_PUBLIC, meta.getterName, "()" + meta.desc);
        cn.methods.add(mn);
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }
}
