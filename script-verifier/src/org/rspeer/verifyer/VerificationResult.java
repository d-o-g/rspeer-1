package org.rspeer.verifyer;

import com.sun.istack.internal.Nullable;
import org.rspeer.verifyer.decline.DeclineData;
import org.rspeer.verifyer.decline.DeclineReason;

public class VerificationResult {

    private final boolean verified;
    private final DeclineReason reason;

    @Nullable
    private final DeclineData data;

    public VerificationResult(boolean verified) {
        this(verified, DeclineReason.NONE);
    }

    public VerificationResult(boolean verified, DeclineReason reason) {
        this(verified, reason, null);
    }

    public VerificationResult(boolean verified, DeclineReason reason, DeclineData data) {
        this.verified = verified;
        this.data = data;
        this.reason = reason;
    }
}
