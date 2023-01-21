package org.rspeer.verifyer.decline.violation;

public class ClassViolation extends Violation {

    private final String className;

    public ClassViolation(String type, Severity severity, String className) {
        super(type, severity);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClassViolation && ((ClassViolation) obj).className.equals(className);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + className.hashCode();
    }
}
