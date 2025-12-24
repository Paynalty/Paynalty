package com.paynalty.domain.challengeverification;

public enum VerificationStatus {
    FAIL("인증인정안함"),
    SUCCESS("인증인정");

    private final String description;

    VerificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
