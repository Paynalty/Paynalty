package com.paynalty.domain.challengemember;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "챌린지 멤버 정보 응답")
@Getter
@Builder
public class ChallengeMemberResponse {

    @Schema(description = "챌린지 멤버 ID", example = "Long")
    private Long id;

    @Schema(description = "사용자 토스 ID", example = "Long")
    private Long tossId;

    @Schema(description = "사용자 이름", example = "김철수")
    private String userName;

    @Schema(description = "챌린지 ID", example = "Long")
    private Long challengeId;

    @Schema(description = "참여 시간", example = "LocalDateTime")
    private LocalDateTime joinedAt;


    // 다른 사람이 봤을때 이사람이 당일 인증을 했는지 위해 표시. 나중에 수정 예정. 지금은 다 성공.
    @Schema(description = "성공 여부", example = "String")
    private String isSuccess;

    @Schema(description = "종료 날짜", example = "LocalDate")
    private LocalDate endAt;

    public static ChallengeMemberResponse from(ChallengeMember cm) {
        return ChallengeMemberResponse.builder()
                .id(cm.getId())
                .tossId(cm.getUser().getTossId())
                .userName(cm.getUser().getName())
                .challengeId(cm.getChallenge().getId())
                .joinedAt(cm.getJoinedAt())
                .isSuccess(cm.getIsSuccess().toString())
                .endAt(cm.getEndAt())
                .build();
    }
}

