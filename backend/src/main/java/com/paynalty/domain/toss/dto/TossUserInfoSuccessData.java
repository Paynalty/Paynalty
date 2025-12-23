package com.paynalty.domain.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TossUserInfoSuccessData {
    private Long userKey;
    private String scope;
    private List<String> agreedTerms;
    private String name;
    private Long phone;
    private String birthday;
    private String ci;
    private String gender;
    private String email;
}
