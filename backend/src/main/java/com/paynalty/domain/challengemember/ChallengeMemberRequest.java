package com.paynalty.domain.challengemember;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "챌린지 멤버 추가 요청")
public class ChallengeMemberRequest {

    @Schema(description = "챌린지 ID", example = "1", required = true)
    @NotNull(message = "챌린지 ID는 필수입니다")
    private Long challengeId;

    @Schema(description = "초대할 친구 이름", example = "김철수", required = true)
    @NotBlank(message = "초대할 친구 이름은 필수입니다")
    private String inviteeName;

    @Schema(description = "초대할 친구 전화번호", example = "010-1234-5678", required = true)
    @NotBlank(message = "초대할 친구 전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$", 
             message = "전화번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
    private String inviteePhoneNumber;

}

