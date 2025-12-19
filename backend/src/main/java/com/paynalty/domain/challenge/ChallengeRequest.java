package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChallengeRequest {

    @NotBlank(message = "챌린지명은 필수입니다")
    @Size(max = 50, message = "챌린지명은 50자 이하여야 합니다")
    private String title;

    @Size(max = 255, message = "챌린지 소개는 255자 이하여야 합니다")
    private String description;

    @Size(max = 30, message = "카테고리는 30자 이하여야 합니다")
    private String category;

    @NotNull(message = "시작 날짜는 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "종료 날짜는 필수입니다")
    private LocalDate endDate;

    @Schema(description = "인증 주기", example = "3", required = true)
    private int frequency;

    @Schema(description = "패널티 금액", example = "10000", required = true)
    private int penaltyAmount;

    @Schema(description = "인증 가능 시작 시간 (시/분)", example = "13:00")
    private LocalTime verifyStartAt;

    @Schema(description = "인증 가능 종료 시간 (시/분)", example = "18:00")
    private LocalTime verifyEndAt;

    @Schema(description = "인증 횟수", example = "7")
    private int verifyCount;

//    @Schema(description = "사용자 ID (테스트용)", example = "1", required = true)
//    @NotNull(message = "사용자 ID는 필수입니다")
//    private Long userId;

    @Schema(description = "인증 방식", example = "PHOTO", required = true)
    @NotNull(message = "인증 방식은 필수입니다")
    private VerificationType verificationType;

    @Schema(description = "챌린지 상태", example = "progress")
    @Size(max = 20, message = "상태는 20자 이하여야 합니다")
    private String status;
}

