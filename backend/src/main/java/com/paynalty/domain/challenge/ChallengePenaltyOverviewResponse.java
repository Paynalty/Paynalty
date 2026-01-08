package com.paynalty.domain.challenge;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class ChallengePenaltyOverviewResponse {
    // 해당 챌린지 벌금 전체 상황. 현재 존재하는 벌금 내역 총합 + 납부 된 금액 총합 + 미납된 금액
    private Long totalPenalty; // penalty 필드 penaltyAmount 총합
    private Long paidPenalty; // penalty 필드 값중 paid가 true 인 데이터들의 penaltyAmount 총합
    private Long nonPaidPenalty;// paid가 false인 데이터들의 penaltyAmount 총합

    // 내 패널티 현황 주간 단위.
    // 주 단위 기준 챌린지 시작일~ 챌린지 시작일이 포함된 그 주 일요일 사이,
    // 나머지 기본 월요일00:00~일요일23:59:59,
    // 챌린지 마감일: 마감일이 포함된 주는 그주의 월요일 부터 ~ 마감일까지 만약 마감일이 월요일이면 월요일의 데이터만
    // 주간 데이터에 표시할 내용은 : (사용자 기준,챌린지Id 기준) 해당 챌린지에 대한 그주 벌금 처리 상황:
    // (  private Long totalPenalty;
    //    private Long paidPenalty;
    //    private Long nonPaidPenalty;)
    //    필드는 위와 동일. 단 기준이 사용자 기준. 해당 내용을 시작일부터~ 오늘이 포함된 주까지 객체로 담아서 전달
    // [로그인 사용자 기준] 주간 패널티 현황 목록
// - 주차 계산 로직은 Service 계층에서 처리
// - 각 주차는 챌린지 시작일 기준으로 생성됨
// - 마지막 주는 오늘이 포함된 주 또는 챌린지 종료 주까지
// - DTO는 계산 결과만 전달

    // 맴버 별 패널티 현황 주간 단위
    // 내 패널티 현황 주간 단위와 유사
    // 내 패널티와 맴버 별 패너리 주간 단위 따로 구분할 필요없이 해당 챌린지의 주간 현황 불러오는 매서드를 만들고 해당 매서드에서 filter를 통해 사용자 정보 꺼내 사용하기,사용자 제외한 맴버별 정보 꺼내 사용하기

    public ChallengePenaltyOverviewResponse(Long amount1,Long amount2,Long amount3){
        this.totalPenalty = amount1;
        this.paidPenalty = amount2;
        this.nonPaidPenalty = amount3;
    }

}
