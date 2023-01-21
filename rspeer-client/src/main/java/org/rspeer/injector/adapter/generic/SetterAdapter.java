package org.rspeer.injector.adapter.generic;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.api.CodeGenerator;

import java.math.BigInteger;
import java.util.Map;

public class SetterAdapter extends CodeAdapter {

    private final FieldMeta meta;
    private final long decoder;

    public SetterAdapter(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library, FieldMeta meta, long decoder) {
        super(delegate, modscript, library);
        this.meta = meta;
        this.decoder = decoder;
    }

    public SetterAdapter(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library, FieldMeta meta) {
        this(delegate, modscript, library, meta, 0);
    }

    public SetterAdapter(Modscript modscript, Map<String, ClassNode> library, FieldMeta meta, long decoder) {
        this(null, modscript, library, meta, decoder);
    }

    public SetterAdapter(Modscript modscript, Map<String, ClassNode> library, FieldMeta meta) {
        this(null, modscript, library, meta, 0);
    }

    @Override
    public void visitClassNode(ClassNode cn) {

        long encoder = encode(decoder);

        // We only inject non static setter methods
        MethodNode setter = new CodeGenerator()
                .loadLocal(ALOAD, meta.isStatic ? -1 : 0)
                .loadLocal(Type.getType(meta.desc).getOpcode(ILOAD), 1)
                .appendIf(() -> decoder != 0, meta.desc.equals("J") ? new LdcInsnNode(encoder)
                        : new LdcInsnNode((int) encoder))
                .appendIf(() -> decoder != 0, new InsnNode(meta.desc.equals("J") ? LMUL : IMUL))
                .append(new FieldInsnNode(meta.isStatic ? PUTSTATIC : PUTFIELD, meta.owner, meta.name, meta.desc))
                .returning()
                .createPublicMethod(meta.setterName, "(" + meta.desc + ")V");

        cn.methods.add(setter);
    }

    @Override
    public void visitMethodNode(MethodNode mn) {

    }

    private long encode(long num) {
        if (num == 0) {
            return 1;
        }
        int inum = (int) num;
        boolean j = meta.desc.equals("J");
        BigInteger value = BigInteger.valueOf(j ? num : inum).modInverse(BigInteger.ONE.shiftLeft(j ? 64 : 32));
        return j ? value.longValue() : value.intValue();
    }
}
