package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChallengeUpdateRequest {

    private String title;

    @Schema(example = "2026-04-25")
    private LocalDate endDate;

    @Schema(example = "07:00:00")
    private LocalTime verifyStartAt;

    @Schema(example = "15:00:00")
    private LocalTime verifyEndAt;

    @Schema(example = "7777777")
    private Long penaltyAmount;

    @Schema(example = "[\"MON\", \"TUE\", \"WED\", \"THU\", \"FRI\", \"SAT\", \"SUN\"]",
            allowableValues = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"})
    private List<DayOfWeekType> daysOfWeek;

    private Integer frequency;

    private VerificationType verifyType;

    @Schema(example = "[7,8,9]")
    private List<Long> userIds;
   

    // 챌린지 수정 요청 시 챌린지 데이터 조회
    public ChallengeUpdateRequest (Challenge challenge,List<Long> userIds){
        this.title = challenge.getTitle();
        this.endDate = challenge.getEndDate();
        this.verifyStartAt = challenge.getVerifyStartAt();
        this.verifyEndAt = challenge.getVerifyEndAt();
        this.penaltyAmount = challenge.getPenaltyAmount();
        this.daysOfWeek = challenge.getDaysOfWeek();
        this.frequency = challenge.getFrequency();
        this.verifyType = challenge .getVerificationType();
        this.userIds = userIds;
    }

}
