package org.rspeer.verifyer;

import org.objectweb.asm.tree.*;
import org.rspeer.verifyer.asm.ClassLibrary;
import org.rspeer.verifyer.decline.DeclineReason;
import org.rspeer.verifyer.decline.StackTraceDecline;
import org.rspeer.verifyer.decline.ViolationDecline;
import org.rspeer.verifyer.decline.violation.*;
import org.rspeer.verifyer.visitor.VulnerabilityVisitor;

import java.util.HashSet;
import java.util.Set;

// Have to suppress warnings because java generics doesn't allow for dynamic type checking :)
@SuppressWarnings("unchecked")
public class JARVerifier {

    private final ViolationLibrary violations = new ViolationLibrary();
    private final ClassLibrary library;
    private final VulnerabilityVisitor visitor;

    JARVerifier(ClassLibrary library, VulnerabilityVisitor visitor) {
        this.library = library;
        this.visitor = visitor;
    }

    VerificationResult execute() {
        try {
            visitLibrary();

            if (violations.size() == 0) {
                return new VerificationResult(true);
            }

            System.out.println("Found " + violations.size() + " violations with " + visitor.getClass().getSimpleName());

            return new VerificationResult(false, DeclineReason.VIOLATION, new ViolationDecline(violations));
        } catch (Exception e) {
            e.printStackTrace();
            return new VerificationResult(false, DeclineReason.ERROR, new StackTraceDecline(e.getStackTrace()));
        }
    }

    private void visitLibrary() {
        for (ClassNode classNode : library.getClassNodes()) {
            fullAcceptVisitor(classNode);

            for (FieldNode fieldNode : classNode.fields) {
                fullAcceptVisitor(fieldNode);
            }

            for (MethodNode methodNode : classNode.methods) {
                traverseCallTree(methodNode, new HashSet<>());
                fullAcceptVisitor(methodNode);
            }
        }
    }


    private void fullAcceptVisitor(Object object) {
        visitor.acceptIfConsumes(object);
        violations.addAllClassViolations(visitor.getClassViolations());
    }

    private void traverseCallTree(MethodNode methodNode, Set<String> visited) {
        for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            fullAcceptVisitor(insnNode);

            if (insnNode instanceof MethodInsnNode) {
                MethodInsnNode cast = (MethodInsnNode) insnNode;
                if (visited.contains(cast.name + "." + cast.owner)) {
                    continue;
                }

                traverseMethodInsnNode(cast, visited);
            }
        }
    }

    private void traverseMethodInsnNode(MethodInsnNode cast, Set<String> visited) {
        MethodNode call = library.findMethodRecursive(cast, cast.owner);
        if (call == null) {
            return;
        }

        ClassMethodViolation cmv = violations.getMethodViolation(cast.owner, call);
        if (cmv != null) {
            violations.addClassViolation(createCallViolation(cmv, cast, call));
        } else {
            String identifier = call.name + "." + call.owner.name;
            if (!visited.contains(identifier)) {
                visited.add(identifier);
                traverseCallTree(call, visited);
            }
        }
    }

    private ClassViolation createCallViolation(ClassMethodViolation cmv, MethodInsnNode instruction, MethodNode call) {
        if (cmv instanceof MethodCallViolation) {
            MethodCallViolation mcv = (MethodCallViolation) cmv;
            return new IndirectMethodCallViolation(
                    (cmv instanceof IndirectMethodCallViolation) ? cmv.getType() : "Indirect call to: " + cmv.getType(),
                    instruction.method().owner.name,
                    instruction.method().name,
                    call.name + "->" + mcv.getCallName(),
                    call.owner.name + " -> " + mcv.getCallOwner()

            );
        }

        return null;
    }


}
