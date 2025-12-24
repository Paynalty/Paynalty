package com.paynalty.domain.challengebank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeBankResponse {

    private Long challengeBankId;
    private Long challengeId;
    private Integer totalAmount;
    private String status;

    public static ChallengeBankResponse from(ChallengeBank challengeBank) {
        return ChallengeBankResponse.builder()
                .challengeBankId(challengeBank.getId())
                .challengeId(challengeBank.getChallenge().getId())
                .totalAmount(challengeBank.getTotalAmount())
                .status(challengeBank.getStatus())
                .build();
    }
}

