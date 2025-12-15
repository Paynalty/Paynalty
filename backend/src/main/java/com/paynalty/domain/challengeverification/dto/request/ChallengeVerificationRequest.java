package com.paynalty.domain.challengeverification.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ChallengeVerificationRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "챌린지 ID는 필수입니다")
    private Long challengeId;

    @NotNull(message = "인증 날짜는 필수입니다")
    private LocalDate date;

    @Size(max = 500, message = "인증 이미지 URL은 500자 이하여야 합니다")
    private String imageUrl;

    @Size(max = 20, message = "인증 상태는 20자 이하여야 합니다")
    private String status;
}

