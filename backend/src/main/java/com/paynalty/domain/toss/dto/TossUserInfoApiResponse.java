package com.paynalty.domain.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossUserInfoApiResponse {
    private String resultType;
    private TossUserInfoData success;
}
