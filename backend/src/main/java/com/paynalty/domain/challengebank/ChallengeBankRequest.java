package com.paynalty.domain.challengebank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChallengeBankRequest {

    @NotNull(message = "챌린지 ID는 필수입니다")
    private Long challengeId;

    private Integer totalAmount;

    @Size(max = 20, message = "상태는 20자 이하여야 합니다")
    private String status;
}

