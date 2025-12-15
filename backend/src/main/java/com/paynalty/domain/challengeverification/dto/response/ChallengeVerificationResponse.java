package com.paynalty.domain.challengeverification.dto.response;

import com.example.paynalty.domain.challengeverification.entity.ChallengeVerification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ChallengeVerificationResponse {

    private Long id;
    private Long userId;
    private Long challengeId;
    private LocalDate date;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;

    public static ChallengeVerificationResponse from(ChallengeVerification cv) {
        return ChallengeVerificationResponse.builder()
                .id(cv.getId())
                .userId(cv.getUser().getId())
                .challengeId(cv.getChallenge().getId())
                .date(cv.getDate())
                .imageUrl(cv.getImageUrl())
                .status(cv.getStatus())
                .createdAt(cv.getCreatedAt())
                .build();
    }
}

