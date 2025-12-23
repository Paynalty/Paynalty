package com.paynalty.domain.toss.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class TossUserInfoErrorData {

    private Integer errorType;
    private String errorCode;
    private String reason;
    private Map<String, Object> data;
    private String title;
}
