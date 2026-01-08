package com.paynalty.domain.penalty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PenaltyOverview {
    private Long totalPenalty;
    private Long paidPenalty;
    private Long nonPaidPenalty;

    public PenaltyOverview(Long totalPenalty,Long paidPenalty,Long nonPaidPenalty){
        this.totalPenalty = totalPenalty;
        this.paidPenalty = paidPenalty;
        this.nonPaidPenalty = nonPaidPenalty;
    }

}
