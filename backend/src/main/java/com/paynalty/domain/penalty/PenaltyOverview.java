package com.paynalty.domain.penalty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PenaltyOverview {
    private Long totalPenalty;
    private Long paidPenalty;
    private Long nonPaidPenalty;


}
