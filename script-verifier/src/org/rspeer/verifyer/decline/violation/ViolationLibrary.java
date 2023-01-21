package org.rspeer.verifyer.decline.violation;

import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViolationLibrary {

    private final Object lock = new Object();
    private final Map<String, Set<ClassViolation>> classViolations = new HashMap<>();

    public void addClassViolation(ClassViolation violation) {
        String node = violation.getClassName();
        if (this.classViolations.containsKey(node)) {
            this.classViolations.get(node).add(violation);
        } else {
            Set<ClassViolation> nodeViolations = new HashSet<>();
            nodeViolations.add(violation);
            this.classViolations.put(node, nodeViolations);
        }
    }


    public void addAllClassViolations(Map<String, Set<ClassViolation>> violations) {
        for (String key : violations.keySet()) {
            Set<ClassViolation> set = violations.get(key);
            if (set.isEmpty()) {
                continue;
            }

            this.classViolations.put(key, violations.get(key));
        }
    }

    public Set<ClassViolation> getViolationsForClass(String cn) {
        return classViolations.getOrDefault(cn, new HashSet<>());
    }

    public ClassMethodViolation getMethodViolation(String cn, MethodNode node) {
        if (!classViolations.containsKey(cn)) {
            return null;
        }

        Set<ClassViolation> violations = classViolations.get(cn);
        for (ClassViolation violation : violations) {
            if (!(violation instanceof ClassMethodViolation)) {
                continue;
            }

            ClassMethodViolation cmv = (ClassMethodViolation) violation;
            if (cmv.getMethodName().equals(node.name)) {
                return cmv;
            }
        }

        return null;
    }

    public Map<String, Set<ClassViolation>> getClassViolations() {
        return classViolations;
    }

    public int size() {
        return classViolations.size();
    }

    public void removeDuplicates() {
        for (String string : classViolations.keySet()) {

        }
    }
}
