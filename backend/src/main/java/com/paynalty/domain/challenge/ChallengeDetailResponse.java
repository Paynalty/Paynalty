package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "챌린지 상세 정보 응답")
@Getter
@Builder
public class ChallengeDetailResponse {

    @Schema(description = "오늘의 인증 상태 (VERIFIED: 인증함, NOT_VERIFIED: 인증안함)", example = "VerificationStatus 타입, 종류 : VERIFIED, NOT_VERIFIED")
    private VerificationStatus verificationStatus;

    @Schema(description = "챌린지 ID", example = "Integer")
    private Long id;

    @Schema(description = "챌린지 제목", example = "String")
    private String title;

    @Schema(description = "현재 주간 인증 횟수", example = "Integer")
    private Integer weeklyProgressCount;

    @Schema(description = "주간 총 인증 횟수 (목표)", example = "Integer")
    private Integer weeklyRequiredCount;

    @Schema(description = "인증 실패 시 벌금", example = "Long")
    private Long penaltyAmount;

    // 인증 시작 시간( 00:00)
    private LocalTime verifyStart;
    // 인증 마감 시간
    private LocalTime verifyEnd;
    //인증 요일
    private List<DayOfWeekType> daysOfWeek;

    //마감일(년/월/일)
    private LocalDate endAt;
}


