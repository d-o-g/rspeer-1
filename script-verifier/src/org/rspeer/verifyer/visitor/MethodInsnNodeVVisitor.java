package org.rspeer.verifyer.visitor;

import org.objectweb.asm.tree.MethodInsnNode;

public abstract class MethodInsnNodeVVisitor extends VulnerabilityVisitor<MethodInsnNode> {

    @Override
    public boolean consumes(Object object) {
        return object instanceof MethodInsnNode;
    }

}
