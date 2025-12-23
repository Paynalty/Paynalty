package com.paynalty.domain.challenge;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class ChallengeResponse {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int frequency;
    private Long penaltyAmount;
    private String status;
    private LocalTime verifyStartAt;
    private LocalTime verifyEndAt;
    private VerificationType verificationType;

    // 인증 하는 요일 (MON, TUE, WED, THU, FRI, SAT, SUN)
    private List<DayOfWeekType> dayOfWeeks;

    public static ChallengeResponse from(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .frequency(challenge.getFrequency())
                .penaltyAmount(challenge.getPenaltyAmount())
                .status(challenge.calculateStatus())  // 자동 계산된 status 사용
                .verifyStartAt(challenge.getVerifyStartAt())
                .verifyEndAt(challenge.getVerifyEndAt())
                .verificationType(challenge.getVerificationType())
                .dayOfWeeks(challenge.getDayOfWeeks())
                .build();
    }
}

