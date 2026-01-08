package com.paynalty.domain.challengebank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "챌린지 은행 정보 응답")
@Getter
@Builder
public class ChallengeBankResponse {

    @Schema(description = "챌린지 은행 ID", example = "Long")
    private Long challengeBankId;

    @Schema(description = "챌린지 ID", example = "Long")
    private Long challengeId;

    @Schema(description = "총 금액", example = "Integer")
    private Integer totalAmount;

    @Schema(description = "상태", example = "String")
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

