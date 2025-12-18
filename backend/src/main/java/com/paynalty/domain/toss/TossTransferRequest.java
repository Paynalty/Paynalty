package com.paynalty.domain.toss;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 토스 송금 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossTransferRequest {
    @JsonProperty("from_account_id")
    private String fromAccountId;

    @JsonProperty("to_account_id")
    private String toAccountId;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("memo")
    private String memo;
}

