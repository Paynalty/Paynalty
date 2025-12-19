package com.paynalty.domain.challenge;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ChallengeResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private int frequency;
    private int penaltyAmount;
    private String status;
    private LocalTime verifyStartAt;
    private LocalTime verifyEndAt;
    private int verifyCount;
    private VerificationType verificationType;
    private LocalDateTime createdAt;

    public static ChallengeResponse from(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .category(challenge.getCategory())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .frequency(challenge.getFrequency())
                .penaltyAmount(challenge.getPenaltyAmount())
                .status(challenge.getStatus())
                .verifyStartAt(challenge.getVerifyStartAt())
                .verifyEndAt(challenge.getVerifyEndAt())
                .verifyCount(challenge.getVerifyCount())
                .verificationType(challenge.getVerificationType())
                .createdAt(challenge.getCreatedAt())
                .build();
    }
}

