package com.paynalty.domain.challengeverification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MembersVerificationCountResponse {
    private Long userId;
    private String userName;
    private Long verificationCount;
}
