package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Schema(description = "챌린지 상세 정보 응답")
@Getter
@Builder
public class ChallengeDetailResponse {

    @Schema(description = "챌린지 ID", example = "1")
    private Long challengeId;

    @Schema(description = "챌린지 제목", example = "매일 운동하기")
    private String challengeTitle;

    @Schema(description = "현재 주간 인증 횟수", example = "3")
    private int currentWeeklyVerificationCount;

    @Schema(description = "주간 총 인증 횟수 (목표)", example = "7")
    private int weeklyRequiredVerificationCount;

    @Schema(description = "인증 실패 시 벌금", example = "10000")
    private int penaltyAmount;

    @Schema(description = "당일 인증 마감까지 남은 시간 (시:분:초 형식)", example = "01:00:00")
    private String remainingTimeFormatted;
}


