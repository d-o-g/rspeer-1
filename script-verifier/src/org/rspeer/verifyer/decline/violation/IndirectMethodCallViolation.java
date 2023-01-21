package org.rspeer.verifyer.decline.violation;

public class IndirectMethodCallViolation extends MethodCallViolation {

    public IndirectMethodCallViolation(String type, String className, String methodName, String callName, String callOwner) {
        super(type, Severity.LOW, className, methodName, callName, callOwner);
    }
}
