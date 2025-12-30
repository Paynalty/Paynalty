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

    @Schema(description = "인증 ID", example = "Long")
    private Long challengeVerificationId;

    @Schema(description = "인증한 사용자 이름", example = "String")
    private String authName;

    @Schema(description = "챌린지 ID", example = "Long")
    private Long challengeId;

    // createdAt
    @Schema(description = "인증 날짜", example = "LocalDate")
    private LocalDateTime date;

    @Schema(description = "인증 이미지 URL", example = "String")
    private String imageUrl;

    //default: PENDING
    @Schema(description = "인증 상태", example = "VerificationStatus 타입, 종류 : SUCCESS, FAILED, PENDING")
    private VerificationStatus status;

    public static ChallengeVerificationResponse from(ChallengeVerification cv) {
        return ChallengeVerificationResponse.builder()
                .challengeVerificationId(cv.getId())
                .authName(cv.getUser().getName())
                .challengeId(cv.getChallenge().getId())
                .date(cv.getCreatedAt())
                .imageUrl(cv.getImageUrl())
                .build();
    }
}

