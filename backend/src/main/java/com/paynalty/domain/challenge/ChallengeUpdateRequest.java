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

    private LocalDate startDate;

    private LocalDate endDate;

    @Schema(example = "07:00:00")
    private LocalTime verifyStartAt;

    @Schema(example = "15:00:00")
    private LocalTime verifyEndAt;

    private Long penaltyAmount;

    private List<DayOfWeekType> daysOfWeek;

    private Integer frequency;

    private VerificationType verifyType;
    // 기존 생성 로직에 기존 request에서 InviteFriend 데이터를 담은 List가 있고 해당 값을 토대로 챌린지 맴버를 만듬
    //  이후 수정하기, or 맴버 초대,추방 에대한 로직을 위해 updateRequest에 해당값이 필요함.

    public ChallengeUpdateRequest (Challenge challenge){
        this.title = challenge.getTitle();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.verifyStartAt = challenge.getVerifyStartAt();
        this.verifyEndAt = challenge.getVerifyEndAt();
        this.penaltyAmount = challenge.getPenaltyAmount();
        this.daysOfWeek = challenge.getDaysOfWeek();
        this.frequency = challenge.getFrequency();
        this.verifyType = challenge .getVerificationType();
    }

}
