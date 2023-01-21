package org.rspeer.verifyer.visitor;

import org.objectweb.asm.tree.ClassNode;

public abstract class ClassNodeVVisitor extends VulnerabilityVisitor<ClassNode> {

    @Override
    public boolean consumes(Object object) {
        return object instanceof ClassNode;
    }

}
