package org.rspeer.injector.api;


import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public enum Operation {

    MULTIPLY(IMUL),
    DIVIDE(IDIV),
    SUBTRACT(ISUB),
    ADD(IADD);

    private final int baseOpcode;

    Operation(int baseOpcode) {
        this.baseOpcode = baseOpcode;
    }

    public static Operation define(int opcode) {
        if (opcode >= IMUL && opcode <= DMUL) {
            return MULTIPLY;
        } else if (opcode >= IDIV && opcode <= DDIV) {
            return DIVIDE;
        } else if (opcode >= ISUB && opcode <= DSUB) {
            return SUBTRACT;
        } else if (opcode >= IADD && opcode <= DADD) {
            return ADD;
        }
        throw new IllegalArgumentException("Unknown operation: opcode " + opcode);
    }

    public static Operation define(char identifier) {
        if (identifier == '*') {
            return MULTIPLY;
        } else if (identifier == '+') {
            return ADD;
        } else if (identifier == '-') {
            return SUBTRACT;
        } else if (identifier == '/') {
            return DIVIDE;
        }
        throw new IllegalArgumentException("Unknown operation: identifier " + identifier);
    }

    public int getBaseOpcode() {
        return baseOpcode;
    }

    public int getOpcode(String desc) {
        return Type.getReturnType(desc).getOpcode(baseOpcode);
    }
}
