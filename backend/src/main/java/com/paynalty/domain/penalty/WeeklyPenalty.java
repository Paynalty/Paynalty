package com.paynalty.domain.penalty;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WeeklyPenalty {

    // 해당 주의 시작일
    private LocalDateTime startAt;
    // 해당 주의 마지막날
    private LocalDateTime endAt;

    // 해당 주의 총 벌금, 납부한 금액, 미납 금액
    private Long totalAmount;
    private Long paidAmount;
    private Long nonPaidAmount;

    // 해당 주의 패널티 정보 모음
    private List<PenaltyResponse> penaltyList;

}
