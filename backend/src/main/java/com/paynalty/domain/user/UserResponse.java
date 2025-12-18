package com.paynalty.domain.user;

import com.paynalty.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String profileImageUrl;
    private Long tossId;
    private String nickname;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .tossId(user.getTossId())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

