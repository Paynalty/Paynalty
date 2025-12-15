package com.paynalty.domain.penalty.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PenaltyRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "챌린지 ID는 필수입니다")
    private Long challengeId;

    @NotNull(message = "벌금 금액은 필수입니다")
    @Positive(message = "벌금 금액은 양수여야 합니다")
    private Integer amount;

    @Size(max = 255, message = "발생 사유는 255자 이하여야 합니다")
    private String reason;
}

