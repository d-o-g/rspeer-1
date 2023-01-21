package org.rspeer.verifyer.visitor.reflection;

import org.objectweb.asm.tree.ClassNode;
import org.rspeer.verifyer.decline.violation.ClassViolation;
import org.rspeer.verifyer.decline.violation.Severity;
import org.rspeer.verifyer.visitor.ClassNodeVVisitor;
import org.rspeer.verifyer.visitor.PackageBases;

public class VisitReflectionImpl extends ClassNodeVVisitor {

    @Override
    public void accept(ClassNode classNode) {
        if (classNode.superName.contains(PackageBases.REFLECTION_BASE)) {
            addClassViolation(classNode.name, new ClassViolation("Reflection Implemented", Severity.MEDIUM, classNode.name));
        }
    }
}
