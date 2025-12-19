package com.paynalty.domain.penalty;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PenaltyResponse {

    private Long id;
    private Long userId;
    private Long challengeId;
    private Integer amount;
    private String reason;

    // 결제 관련 정보
    private boolean paid;
    private LocalDateTime paidAt;
    private String paymentOrderId;

    private LocalDateTime createdAt;

    public static PenaltyResponse from(Penalty penalty) {
        return PenaltyResponse.builder()
                .id(penalty.getId())
                .userId(penalty.getUser().getId())
                .challengeId(penalty.getChallenge().getId())
                .amount(penalty.getAmount())
                .reason(penalty.getReason())
                .paid(penalty.isPaid())
                .paidAt(penalty.getPaidAt())
                .paymentOrderId(penalty.getPaymentOrderId())
                .createdAt(penalty.getCreatedAt())
                .build();
    }
}

