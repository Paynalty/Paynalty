package com.paynalty.domain.challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @Size(max = 20, message = "상태는 20자 이하여야 합니다")
    private String status;
}

