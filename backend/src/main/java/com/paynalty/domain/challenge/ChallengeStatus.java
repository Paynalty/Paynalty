package com.paynalty.domain.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeStatus {
    PENDING("시작 전"),
    ACTIVE("진행 중"),
    COMPLETED("완료됨");

    private final String description;
}
