package com.paynalty.domain.penalty;

import com.paynalty.domain.challengemember.ChallengeMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "벌금 정보 응답")
@Getter
@Builder
public class PenaltyResponse {

    @Schema(description = "벌금 ID", example = "Long")
    private Long penaltyId;

    @Schema(description = "챌린지 멤버 ID", example = "Long")
    private Long challengeMemberId;

    @Schema(description = "챌린지 제목", example = "String")
    private String challengeTitle;

    @Schema(description = "벌금액", example = "Long")
    private Long amount;

    // 결제 관련 정보
    @Schema(description = "결제 여부", example = "Boolean")
    private Boolean paid;

    @Schema(description = "결제 시간", example = "LocalDateTime")
    private LocalDateTime paidAt;

    @Schema(description = "생성 시간", example = "LocalDateTime")
    private LocalDateTime createdAt;

    public static PenaltyResponse from(Penalty penalty) {
        ChallengeMember member = penalty.getChallengeMember();
        return PenaltyResponse.builder()
                .penaltyId(penalty.getId())
                .challengeMemberId(member.getId())
                .challengeTitle(member.getChallenge().getTitle())
                .amount(penalty.getPenaltyAmount())
                .paid(penalty.getPaid())
                .paidAt(penalty.getPaidAt())
                .createdAt(penalty.getCreatedAt())
                .build();
    }
}

