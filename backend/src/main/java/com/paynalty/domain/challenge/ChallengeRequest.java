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

    @NotNull(message = "시작 날짜는 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "종료 날짜는 필수입니다")
    private LocalDate endDate;

    @Schema(description = "인증 주기", example = "3", required = true)
    private int frequency;

    @Schema(description = "패널티 금액", example = "10000", required = true)
    private int penaltyAmount;

    @Schema(description = "인증 가능 시작 시간 (시/분)", example = "00:00")
    private LocalTime verifyStartAt;

    @Schema(description = "인증 가능 종료 시간 (시/분)", example = "23:59")
    private LocalTime verifyEndAt;


    @Schema(description = "인증 방식", example = "PHOTO", required = true)
    @NotNull(message = "인증 방식은 필수입니다")
    private VerificationType verificationType;

}

