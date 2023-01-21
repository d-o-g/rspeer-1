package org.rspeer.verifyer.decline.violation;

public abstract class ClassMethodViolation extends ClassViolation {

    private final String methodName;

    public ClassMethodViolation(String type, Severity severity, String className, String methodName) {
        super(type, severity, className);
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof ClassMethodViolation && ((ClassMethodViolation) obj).methodName.equals(methodName);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + methodName.hashCode();
    }
}
