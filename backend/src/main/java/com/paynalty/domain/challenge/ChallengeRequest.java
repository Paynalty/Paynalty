package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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

    private Integer frequency;

    private Integer penaltyAmount;

    @Schema(description = "사용자 ID (테스트용)", example = "1", required = true)
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @Schema(description = "챌린지 상태", example = "진행중")
    @Size(max = 20, message = "상태는 20자 이하여야 합니다")
    private String status;
}

