package com.paynalty.domain.challengeverification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "챌린지 인증 응답")
@Getter
@Builder
public class ChallengeVerificationResponse {

    private Long id;
    private String authEmail;
    private Long challengeId;
    private LocalDate date;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;

    public static ChallengeVerificationResponse from(ChallengeVerification cv) {
        return ChallengeVerificationResponse.builder()
                .id(cv.getId())
                .authEmail(cv.getUser().getEmail())
                .challengeId(cv.getChallenge().getId())
                .date(cv.getDate())
                .imageUrl(cv.getImageUrl())
                .status(cv.getStatus())
                .createdAt(cv.getCreatedAt())
                .build();
    }
}

