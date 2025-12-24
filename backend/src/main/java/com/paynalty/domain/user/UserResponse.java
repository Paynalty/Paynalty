package com.paynalty.domain.user;

import com.paynalty.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private Long tossId;
    private String name;
    private String email;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .tossId(user.getTossId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

