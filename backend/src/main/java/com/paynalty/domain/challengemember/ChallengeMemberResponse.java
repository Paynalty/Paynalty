package com.paynalty.domain.challengemember;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ChallengeMemberResponse {

    private Long id;
    private Long userId;
    private Long challengeId;
    private LocalDateTime joinedAt;
    private String isSuccess;
    private LocalDate endAt;

    public static ChallengeMemberResponse from(ChallengeMember cm) {
        return ChallengeMemberResponse.builder()
                .id(cm.getId())
                .userId(cm.getUser().getId())
                .challengeId(cm.getChallenge().getId())
                .joinedAt(cm.getJoinedAt())
                .isSuccess(cm.getIsSuccess().toString())
                .endAt(cm.getEndAt())
                .build();
    }
}

