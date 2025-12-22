package com.paynalty.domain.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossTokenResponse {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private String expiresIn;
    private Integer refreshTokenExpiresIn;
    private String scope;
}
