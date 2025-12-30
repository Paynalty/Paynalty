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

    @Schema(description = "사용자 ID", example = "Long")
    private Long userId;

    @Schema(description = "사용자 이름", example = "김철수")
    private String userName;

    @Schema(description = "챌린지 ID", example = "Long")
    private Long challengeId;

    @Schema(description = "참여 시간", example = "LocalDateTime")
    private LocalDateTime joinedAt;

    // 챌린지에 대한 성공 실패 여부 x
    // 참여한 챌린지에 대한 인증 여부 - > 했다 ture , 안했다 false
    // 인증 조건에 맞는 인증 데이터 있는지 확인 -> 인증 성공 = true or 인증 실패 = false   // 보류 - 인증 데이터 확정 후 수정
    @Schema(description = "성공 여부", example = "String")
    private String isSuccess;

    @Schema(description = "종료 날짜", example = "LocalDate")
    private LocalDate endAt;

    public static ChallengeMemberResponse from(ChallengeMember cm) {
        return ChallengeMemberResponse.builder()
                .id(cm.getId())
                .userId(cm.getUser().getId())
                .userName(cm.getUser().getName())
                .challengeId(cm.getChallenge().getId())
                .joinedAt(cm.getJoinedAt())
                .isSuccess(cm.getIsSuccess().toString())
                .endAt(cm.getEndAt())
                .build();
    }
}

