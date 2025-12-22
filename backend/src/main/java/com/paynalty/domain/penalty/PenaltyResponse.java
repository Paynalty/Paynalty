package com.paynalty.domain.penalty;

import com.paynalty.domain.challengemember.ChallengeMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PenaltyResponse {

    private Long id;
    private Long challengeMemberId;
    private Long userId;
    private Long challengeId;
    private String challengeTitle;
    private Integer amount;

    // 결제 관련 정보
    private boolean paid;
    private LocalDateTime paidAt;
    private String paymentOrderId;

    private LocalDateTime createdAt;

    public static PenaltyResponse from(Penalty penalty) {
        ChallengeMember member = penalty.getChallengeMember();
        return PenaltyResponse.builder()
                .id(penalty.getId())
                .challengeMemberId(member.getId())
                .userId(member.getUser().getId())
                .challengeId(member.getChallenge().getId())
                .challengeTitle(member.getChallenge().getTitle())
                .amount(penalty.getFixedAmount())
                .createdAt(penalty.getCreatedAt())
                .build();
    }
}

