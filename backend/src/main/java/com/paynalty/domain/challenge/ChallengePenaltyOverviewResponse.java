package com.paynalty.domain.challenge;

import com.paynalty.domain.penalty.MemberPenalty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class ChallengePenaltyOverviewResponse {
    // 해당 챌린지 벌금 전체 상황. 현재 존재하는 벌금 내역 총합 + 납부 된 금액 총합 + 미납된 금액
    private Long totalPenalty; // penalty 필드 penaltyAmount 총합
    private Long paidPenalty; // penalty 필드 값중 paid가 true 인 데이터들의 penaltyAmount 총합
    private Long nonPaidPenalty;// paid가 false인 데이터들의 penaltyAmount 총합

    private List<MemberPenalty> memberPenaltyList;



}
