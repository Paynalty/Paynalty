package com.paynalty.domain.challengeverification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "챌린지 인증 응답")
@Getter
@Builder
public class ChallengeVerificationResponse {

    @Schema(description = "인증 ID", example = "Long")
    private Long id;

    @Schema(description = "인증한 사용자 이름", example = "String")
    private String userName;

    @Schema(description = "챌린지 ID", example = "Long")
    private Long challengeId;

    // createdAt
    @Schema(description = "인증 날짜", example = "LocalDate")
    private LocalDateTime dateTime;

    @Schema(description = "인증 이미지 URL", example = "String")
    private String imageUrl;

    //보류
//    @Schema(description = "인증 상태", example = "VerificationStatus 타입, 종류 : SUCCESS, FAILED, PENDING")
//    private VerificationStatus status;

    public static ChallengeVerificationResponse from(ChallengeVerification cv) {
        // 이미지 URL이 있으면 전체 경로로 변환
        String imageUrl = cv.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // 파일명만 저장되어 있으므로 서빙 경로 추가
            imageUrl = "/uploads/verifications/" + imageUrl;
        }
        
        return ChallengeVerificationResponse.builder()
                .id(cv.getId())
                .userName(cv.getUser().getName())
                .challengeId(cv.getChallenge().getId())
                .dateTime(cv.getVerifiedAt())
                .imageUrl(imageUrl)
                .build();
    }
}

