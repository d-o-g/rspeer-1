package org.rspeer.verifyer.decline;

public class StackTraceDecline implements DeclineData {

    private final StackTraceElement[] stackTrace;

    public StackTraceDecline(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String message() {
        return "The script was declined because the automated script verifier had a runtime exception.";
    }

}
