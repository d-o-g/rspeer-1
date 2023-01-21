package org.rspeer.verifyer.decline.violation;

public class ClassFieldViolation extends ClassViolation {

    private final String fieldName;

    public ClassFieldViolation(String type, Severity severity, String className, String fieldName) {
        super(type, severity, className);
        this.fieldName = fieldName;
    }
}
