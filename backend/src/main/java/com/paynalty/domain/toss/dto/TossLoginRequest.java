package com.paynalty.domain.toss.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossLoginRequest {
    private String authorizationCode;
    private String referrer;

    // generate-token
    @Builder
    public TossLoginRequest(String authorizationCode, String referrer) {
        this.authorizationCode = authorizationCode;
        this.referrer = referrer;
    }
}
