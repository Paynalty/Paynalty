package com.paynalty.domain.challenge;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class ChallengeResponse {

    private Long challengeId;
    private String title;
//    private LocalDate startDate;
//    private LocalDate endDate;

    // 사용자 현재 인증 횟수 변수 추가

    //  인증 주기 dayOfWeek 추가
    private int frequency;

    private Long penaltyAmount;

    // 인증 상태에 따른 참여 완료 ? 지금 할 차례에요 메세지 전달.
    // 해당 변수 미사용. 타입 변경하여 사용 예정
    private ChallengeStatus status;

    private LocalTime verifyStartAt;
    private LocalTime verifyEndAt;
    private VerificationType verificationType;

    // 당일 인증 안했을시 마감시간 표시 추가
    // 챌린지 내 최신 인증 한 데이터 추가
    // 주간 인증 현황 추가

    // 인증 하는 요일 (MON, TUE, WED, THU, FRI, SAT, SUN)
    private List<DayOfWeekType> dayOfWeeks;

    public static ChallengeResponse from(Challenge challenge) {
        return ChallengeResponse.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
//                .startDate(challenge.getStartDate())
//                .endDate(challenge.getEndDate())
                .frequency(challenge.getFrequency())
                .penaltyAmount(challenge.getPenaltyAmount())
                .status(challenge.calculateStatus())  // 자동 계산된 status 사용
                .verifyStartAt(challenge.getVerifyStartAt())
                .verifyEndAt(challenge.getVerifyEndAt())
                .verificationType(challenge.getVerificationType())
                .dayOfWeeks(challenge.getDaysOfWeek())
                .build();
    }
}

