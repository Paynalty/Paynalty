package com.paynalty.domain.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossTokenResponse {

    private String resultType;
    private SuccessData success;
    private TossUserInfoErrorData error;

    @Getter
    @NoArgsConstructor
    public static class SuccessData {
        private String accessToken;
        private String tokenType;
        private String refreshToken;
        private String expiresIn;
        private String scope;
    }
}
