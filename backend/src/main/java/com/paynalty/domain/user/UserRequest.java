package com.paynalty.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Schema(description = "사용자 이메일", example = "test@test.com")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다")
    private String email;

    @Schema(description = "토스 id", example = "1298")
    private Long tossId;

    private String name;
    private String phoneNum;


}

