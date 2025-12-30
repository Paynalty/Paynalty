package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "챌린지 상세 정보 응답")
@Getter
@Builder
public class ChallengeResponse {

    @Schema(description = "챌린지 ID", example = "Long")
    private Long id;

    @Schema(description = "챌린지 제목", example = "String")
    private String title;

    @Schema(description = "참여 멤버 수", example = "Integer")
    private Integer totalParticipants;

    @Schema(description = "챌린지 시작일", example = "LocalDate")
    private LocalDate startDate;

    @Schema(description = "챌린지 종료일", example = "LocalDate")
    private LocalDate endDate;

    @Schema(
            description = "주간 인증 횟수 (1일 1회만 인증 가능)\n" +
                    "- daysOfWeek가 설정된 경우: 요일 개수와 동일\n" +
                    "- daysOfWeek가 없는 경우: 사용자가 자유롭게 선택한 주간 인증 횟수",
            example = "Integer"
    )
    private Integer frequency;

    @Schema(description = "인증 실패 시 벌금액", example = "Long")
    private Long penaltyAmount;

    @Schema(description = "오늘의 인증 상태 (사용자가 오늘 인증했는지 여부)", example = "VerificationStatus 타입, 종류 : VERIFIED, NOT_VERIFIED")
    private VerificationStatus verificationStatus;

    @Schema(description = "현재 주간 인증 횟수 (이번 주에 사용자가 완료한 인증 수)", example = "Integer")
    private Integer weeklyProgressCount;

    @Schema(description = "인증 가능 시작 시간", example = "LocalTime")
    private LocalTime verifyStartAt;

    @Schema(description = "인증 마감 시간", example = "LocalTime")
    private LocalTime verifyEndAt;

    @Schema(description = "인증 방식", example = "VerificationType 타입, 종류 : PHOTO, TEXT, VOTE")
    private VerificationType verificationType;

    @Schema(
            description = "인증 요일 목록 (특정 요일에만 인증하는 경우)\n" +
                    "- null 또는 빈 리스트: 요일 지정 없음 (주간 frequency 횟수만큼 자유롭게 인증)\n" +
                    "- 값 있음: 지정된 요일에만 인증 가능",
            example = "List<DayOfWeekType> 타입, 종류 : MON, TUE, WED, THU, FRI, SAT, SUN"
    )
    private List<DayOfWeekType> daysOfWeek;

    /**
     * Challenge 엔티티와 추가 정보로부터 ChallengeResponse를 생성합니다.
     * 
     * @param challenge Challenge 엔티티
     * @param verificationStatus 오늘의 인증 상태 (VERIFIED: 인증함, NOT_VERIFIED: 인증안함)
     * @param totalParticipants 참여 멤버 수
     * @param weeklyProgressCount 현재 주간 인증 횟수
     * @return ChallengeResponse
     */
    public static ChallengeResponse from(
            Challenge challenge, 
            VerificationStatus verificationStatus,
            Integer totalParticipants,
            Integer weeklyProgressCount
    ) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .totalParticipants(totalParticipants)
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .frequency(challenge.getFrequency())
                .penaltyAmount(challenge.getPenaltyAmount())
                .verificationStatus(verificationStatus)
                .weeklyProgressCount(weeklyProgressCount)
                .verifyStartAt(challenge.getVerifyStartAt())
                .verifyEndAt(challenge.getVerifyEndAt())
                .verificationType(challenge.getVerificationType())
                .daysOfWeek(challenge.getDaysOfWeek())
                .build();
    }
}

