package com.paynalty.domain.toss.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TossUserInfoData { //이름, 이메일, 성별, 생년월일, 전화번호
    private Long userKey;
    private String scope;
    private List<List<String>> agreedTerms;
    private String name;
    private String email;
    private String gender;
    private String birthday;
    private Long phone;
}
