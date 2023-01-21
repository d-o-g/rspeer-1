package org.rspeer.injector.api;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.rspeer.injector.hook.FieldHook;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public final class CodeGenerator implements Opcodes {

    private final InsnList instructions;

    public CodeGenerator() {
        instructions = new InsnList();
    }

    public CodeGenerator loadConstant(Object value) {
        return append(new LdcInsnNode(value));
    }

    public MethodNode createMethod(int access, String name, String desc) {
        MethodNode mn = new MethodNode(access, name, desc, null, null);
        mn.instructions = instructions;
        return mn;
    }

    public MethodNode createPublicMethod(String name, String desc) {
        return createMethod(ACC_PUBLIC, name, desc);
    }

    public CodeGenerator loadField(int var, boolean isStatic, String owner, String name, String desc) {
        if (!isStatic && var != -1) {
            append(new VarInsnNode(ALOAD, var));
        }
        return append(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, owner, name, desc));
    }

    public CodeGenerator loadField(int var, FieldHook hook) {
        return loadField(var, hook.isStatic(), hook.getOwner(), hook.getInternalName(), hook.getDesc());
    }

    public CodeGenerator loadLocalField(int var, String owner, String name, String desc) {
        return loadField(var, false, owner, name, desc);
    }

    public CodeGenerator loadStaticField(String owner, String name, String desc) {
        return loadField(-1, true, owner, name, desc);
    }

    public CodeGenerator invokeMethod(int var, boolean isStatic, String owner, String name, String desc) {
        if (!isStatic && var != -1) {
            append(new VarInsnNode(Opcodes.ALOAD, var));
        }
        return append(new MethodInsnNode(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, owner, name, desc, false));
    }

    public CodeGenerator invokeVirtual(int var, String owner, String name, String desc) {
        return invokeMethod(var, false, owner, name, desc);
    }

    public CodeGenerator invokeStatic(String owner, String name, String desc) {
        return invokeMethod(-1, true, owner, name, desc);
    }

    public CodeGenerator loadLocal(int op, int var) {
        if (var == -1) {
            return this;
        }
        return append(new VarInsnNode(op, var));
    }

    public CodeGenerator returningLocal(int varOp, int varIdx, int returnOp) {
        return loadLocal(varOp, varIdx).append(new InsnNode(returnOp));
    }

    public CodeGenerator returningStaticField(String owner, String name, String desc) {
        return loadStaticField(owner, name, desc).append(new InsnNode(Type.getType(desc).getOpcode(Opcodes.IRETURN)));
    }

    public CodeGenerator returningLocalField(int var, String owner, String name, String desc) {
        return loadLocalField(var, owner, name, desc).returning(Type.getType(desc).getOpcode(Opcodes.IRETURN));
    }

    public CodeGenerator returning() {
        return returning(RETURN);
    }

    public CodeGenerator returning(int op) {
        return append(new InsnNode(op));
    }

    public CodeGenerator setLocalField(Function<CodeGenerator, CodeGenerator> value, int var, String owner, String name, String desc) {
        return value.apply(loadLocal(Type.getType(desc).getOpcode(ILOAD), var))
                .append(new FieldInsnNode(PUTFIELD, owner, name, desc));
    }

    public CodeGenerator setFieldToLocal(int destVar, int sourceVar, String owner, String name, String desc) {
        return setLocalField(gen -> gen.loadLocal(Type.getReturnType(desc).getOpcode(ILOAD), destVar), sourceVar, owner, name, desc);
    }

    public CodeGenerator appendIf(BooleanSupplier supplier, CodeGenerator generator) {
        if (supplier.getAsBoolean()) {
            return append(generator);
        }
        return this;
    }

    public CodeGenerator append(CodeGenerator generator) {
        instructions.add(generator.instructions);
        return this;
    }

    public CodeGenerator append(Function<CodeGenerator, CodeGenerator> function) {
        return function.apply(this);
    }

    public CodeGenerator appendIf(BooleanSupplier supplier, AbstractInsnNode ain) {
        if (supplier.getAsBoolean()) {
            return append(ain);
        }
        return this;
    }

    public CodeGenerator append(AbstractInsnNode insn) {
        instructions.add(insn);
        return this;
    }

    public CodeGenerator append(InsnList list) {
        instructions.add(list);
        return this;
    }

    public CodeGenerator appendIf(BooleanSupplier condition, Function<CodeGenerator, CodeGenerator> function) {
        if (condition.getAsBoolean()) {
            return function.apply(this);
        }
        return this;
    }

    public CodeGenerator addExpr(Function<CodeGenerator, CodeGenerator> left, Function<CodeGenerator, CodeGenerator> right, Operation operation) {
        CodeGenerator raw = right.apply(left.apply(new CodeGenerator()));
        String type = ExprHelper.getOperationDesc(raw);
        return append(raw).append(new InsnNode(operation.getOpcode(type)));
    }

    public InsnList collect() {
        return instructions;
    }
}
