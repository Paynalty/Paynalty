package com.paynalty.domain.toss.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponse {
    private String appAccessToken;
    private String userName;
    private boolean isNewUser;
}
