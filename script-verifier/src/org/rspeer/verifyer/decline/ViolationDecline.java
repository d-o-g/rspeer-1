package org.rspeer.verifyer.decline;

import org.rspeer.verifyer.decline.violation.ViolationLibrary;

import java.util.List;

public class ViolationDecline implements DeclineData {

    private final ViolationLibrary violations;

    public ViolationDecline(ViolationLibrary classViolations) {
        this.violations = classViolations;
    }

    @Override
    public String message() {
        return String.format("The script was declined because violations were found in %d classes.", violations.size());
    }
}
