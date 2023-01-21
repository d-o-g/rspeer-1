package org.rspeer.verifyer.visitor.reflection;

import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.verifyer.decline.violation.MethodCallViolation;
import org.rspeer.verifyer.decline.violation.Severity;
import org.rspeer.verifyer.visitor.MethodInsnNodeVVisitor;
import org.rspeer.verifyer.visitor.PackageBases;

public class VisitReflectionMethod extends MethodInsnNodeVVisitor {

    @Override
    public void accept(MethodInsnNode node) {
        if (node.owner.contains(PackageBases.REFLECTION_BASE)) {
            MethodNode containing = node.method();
            addClassViolation(containing.owner.name, new MethodCallViolation("Reflection Method Call", Severity.MEDIUM, containing.owner.name, containing.name, node.name, node.owner));
        }
    }

}
