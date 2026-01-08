package com.paynalty.domain.challengeverification;

public enum VerificationStatus {
    UNVERIFIED("검증되지않음"),
    VALID("유효"),
    INVALID("무효");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
