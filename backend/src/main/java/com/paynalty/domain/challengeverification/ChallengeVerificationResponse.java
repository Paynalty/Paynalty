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

    private String authName;
    private Long challengeId;
    private LocalDate date;
    private String imageUrl;
    private VerificationStatus status;

    public static ChallengeVerificationResponse from(ChallengeVerification cv) {
        return ChallengeVerificationResponse.builder()
                .authName(cv.getUser().getName())
                .challengeId(cv.getChallenge().getId())
                .date(cv.getDate())
                .imageUrl(cv.getImageUrl())
                .status(VerificationStatus.SUCCESS)
                .build();
    }
}

