package com.paynalty.domain.penalty;

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

    @NotNull(message = "챌린지 멤버 ID는 필수입니다")
    private Long challengeMemberId;

    @NotNull(message = "벌금 금액은 필수입니다")
    @Positive(message = "벌금 금액은 양수여야 합니다")
    private Long amount;
}

