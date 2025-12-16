package com.paynalty.domain.penalty.dto.response;

import com.example.paynalty.domain.penalty.entity.Penalty;
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
    private LocalDateTime createdAt;

    public static PenaltyResponse from(Penalty penalty) {
        return PenaltyResponse.builder()
                .id(penalty.getId())
                .userId(penalty.getUser().getId())
                .challengeId(penalty.getChallenge().getId())
                .amount(penalty.getAmount())
                .reason(penalty.getReason())
                .createdAt(penalty.getCreatedAt())
                .build();
    }
}

