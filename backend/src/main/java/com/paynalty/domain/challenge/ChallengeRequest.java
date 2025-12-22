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

    @Schema(description = "시작 옵션 - \"tomorrow\" (내일부터 시작하기) 또는 \"nextWeek\" (다음주부터 시작하기)", example = "tomorrow", required = true)
    @NotBlank(message = "시작 옵션은 필수입니다")
    private String startOption;

    @NotNull(message = "종료 날짜는 필수입니다")
    private LocalDate endDate;

    @Schema(description = "인증 주기 - designatedDays가 null일 때만 사용 (주에 몇 번 인증할지)", example = "null")
    private Integer frequency;

    @Schema(description = "지정된 요일 목록 (예: [\"월\", \"수\", \"목\", \"토\"]) - 이 값이 있으면 frequency는 자동 계산됨", example = "[\"월\", \"수\", \"목\", \"토\"]")
    private List<String> designatedDays;

    @Schema(description = "패널티 금액", example = "10000", required = true)
    private Long penaltyAmount;

    @Schema(description = "인증 가능 시작 시간 (시/분)", example = "00:00")
    private LocalTime verifyStartAt;

    @Schema(description = "인증 가능 종료 시간 (시/분)", example = "23:59")
    private LocalTime verifyEndAt;


    @Schema(description = "인증 방식", example = "PHOTO", required = true)
    @NotNull(message = "인증 방식은 필수입니다")
    private VerificationType verificationType;

}

