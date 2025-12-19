package com.paynalty.domain.toss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 토스 계좌 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossAccountResponse {
    @JsonProperty("accounts")
    private List<TossAccount> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TossAccount {
        @JsonProperty("account_id")
        private String accountId;

        @JsonProperty("bank_code")
        private String bankCode;

        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("account_holder_name")
        private String accountHolderName;

        @JsonProperty("balance")
        private Long balance;

        @JsonProperty("account_type")
        private String accountType;
    }
}

