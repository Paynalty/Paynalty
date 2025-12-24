package com.paynalty.domain.challenge;

/**
 * 챌린지 인증 타입
 */
public enum VerificationType {
    PHOTO("사진 인증"),
    TEXT("텍스트 인증"),
    VOTE("투표 인증");

    private final String description;

    VerificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

