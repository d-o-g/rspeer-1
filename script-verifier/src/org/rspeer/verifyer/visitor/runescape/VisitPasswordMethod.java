package org.rspeer.verifyer.visitor.runescape;

import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.verifyer.decline.violation.MethodCallViolation;
import org.rspeer.verifyer.decline.violation.Severity;
import org.rspeer.verifyer.visitor.MethodInsnNodeVVisitor;
import org.rspeer.verifyer.visitor.PackageBases;

public class VisitPasswordMethod extends MethodInsnNodeVVisitor {

    @Override
    public void accept(MethodInsnNode node) {
        if (node.name.contains("getPassword") && node.owner.contains(PackageBases.RSPEER_BASE)) {
            MethodNode container = node.method();
            addClassViolation(node.owner, new MethodCallViolation("Password call", Severity.HIGH, container.owner.name, container.name, node.name, node.owner));
        }
    }
}
