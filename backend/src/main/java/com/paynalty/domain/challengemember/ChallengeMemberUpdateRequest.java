package com.paynalty.domain.challengemember;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "챌린지 멤버 목록 수정 요청")
public class ChallengeMemberUpdateRequest {

    @Schema(description = "수정된 챌린지 멤버들의 Toss ID 목록", example = "[1001, 1003, 1004]", required = true)
    @NotNull(message = "멤버 ID 목록은 필수")
    private List<Long> userTossIds;
}
