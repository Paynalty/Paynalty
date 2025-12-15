package com.example.paynalty.domain.challenge.dto.response;

import com.example.paynalty.domain.challenge.entity.Challenge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ChallengeResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer frequency;
    private Integer penaltyAmount;
    private String status;
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
                .createdAt(challenge.getCreatedAt())
                .build();
    }
}

