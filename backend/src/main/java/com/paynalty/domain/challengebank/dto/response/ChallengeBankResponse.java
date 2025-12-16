package com.paynalty.domain.challengebank.dto.response;

import com.example.paynalty.domain.challengebank.entity.ChallengeBank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChallengeBankResponse {

    private Long id;
    private Long challengeId;
    private Integer totalAmount;
    private String status;

    public static ChallengeBankResponse from(ChallengeBank challengeBank) {
        return ChallengeBankResponse.builder()
                .id(challengeBank.getId())
                .challengeId(challengeBank.getChallenge().getId())
                .totalAmount(challengeBank.getTotalAmount())
                .status(challengeBank.getStatus())
                .build();
    }
}

