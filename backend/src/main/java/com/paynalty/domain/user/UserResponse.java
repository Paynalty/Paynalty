package com.paynalty.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "사용자 정보 응답")
@Getter
@Builder
public class UserResponse {

    @Schema(description = "사용자 ID", example = "Long")
    private Long userId;

    @Schema(description = "토스 사용자 ID", example = "Long")
    private Long tossId;

    @Schema(description = "사용자 이름", example = "String")
    private String name;

    @Schema(description = "이메일", example = "String")
    private String email;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .tossId(user.getTossId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}