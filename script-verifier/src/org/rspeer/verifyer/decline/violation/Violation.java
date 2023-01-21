package org.rspeer.verifyer.decline.violation;

public abstract class Violation {

    private final String type;
    private final Severity severity;

    public Violation(String type, Severity severity) {
        this.type = type;
        this.severity = severity;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return type.hashCode() + severity.toString().hashCode();
    }
}
