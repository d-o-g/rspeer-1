package org.rspeer.verifyer.decline.violation;

public class MethodCallViolation extends ClassMethodViolation {

    private final String callOwner;
    private final String callName;
    private final int hashCode;

    public MethodCallViolation(String type, Severity severity, String className, String methodName, String callName, String callOwner) {
        super(type, severity, className, methodName);
        this.callName = callName;
        this.callOwner = callOwner;
        this.hashCode = hashCode();
    }

    public String getCallOwner() {
        return callOwner;
    }

    public String getCallName() {
        return callName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodCallViolation) || !super.equals(obj)) {
            return false;
        }

        MethodCallViolation methodCallViolation = ((MethodCallViolation) obj);
        return methodCallViolation.callName.equals(callName) && methodCallViolation.callOwner.equals(callOwner);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + callOwner.hashCode() + callName.hashCode();
    }
}
