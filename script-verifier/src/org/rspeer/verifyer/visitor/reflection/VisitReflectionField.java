package org.rspeer.verifyer.visitor.reflection;

import org.objectweb.asm.tree.FieldNode;
import org.rspeer.verifyer.decline.violation.ClassFieldViolation;
import org.rspeer.verifyer.decline.violation.Severity;
import org.rspeer.verifyer.visitor.FieldNodeVVisitor;
import org.rspeer.verifyer.visitor.PackageBases;

public class VisitReflectionField extends FieldNodeVVisitor {

    @Override
    public void accept(FieldNode node) {
        if (node.desc.contains(PackageBases.REFLECTION_BASE)) {
            addClassViolation(node.owner.name, new ClassFieldViolation("Reflection Field", Severity.MEDIUM, node.owner.name, node.name));
        }
    }
}
