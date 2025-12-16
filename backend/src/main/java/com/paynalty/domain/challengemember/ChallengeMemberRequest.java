package com.paynalty.domain.challengemember;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ChallengeMemberRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "챌린지 ID는 필수입니다")
    private Long challengeId;

    @Size(max = 20, message = "참여 상태는 20자 이하여야 합니다")
    private String isSuccess;

    private LocalDate endAt;
}

