package org.rspeer.verifyer.visitor;

import org.objectweb.asm.tree.FieldNode;

public abstract class FieldNodeVVisitor extends VulnerabilityVisitor<FieldNode> {

    @Override
    public boolean consumes(Object object) {
        return object instanceof FieldNode;
    }

}
