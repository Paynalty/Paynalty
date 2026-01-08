package com.paynalty.domain.penalty;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberPenalty {
    // 맴버 이름
    private String memberName;
    // 맴버 토스 id
    private Long tossId;

    private List<WeeklyPenalty> weeklyPenaltyList;

}
