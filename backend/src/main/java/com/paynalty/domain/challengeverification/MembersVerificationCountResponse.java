package com.paynalty.domain.challengeverification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "멤버별 인증 횟수 응답")
@Getter
@AllArgsConstructor
public class MembersVerificationCountResponse {

    @Schema(description = "사용자 토스 ID", example = "Long")
    private Long tossId;

    @Schema(description = "사용자 이름", example = "String")
    private String userName;

    @Schema(description = "인증 횟수", example = "Long")
    private Long verificationCount;
}
