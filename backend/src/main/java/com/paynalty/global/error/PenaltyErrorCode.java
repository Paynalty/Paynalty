package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PenaltyErrorCode implements ErrorCode {
    // 페널티 관련 에러 (PN001~PN099)
    PENALTY_NOT_FOUND(HttpStatus.NOT_FOUND, "PN001", "Penalty Not Found"),
    PENALTY_ALREADY_PAID(HttpStatus.CONFLICT, "PN002", "Penalty Already Paid"),
    PENALTY_EXPIRED(HttpStatus.BAD_REQUEST, "PN003", "Penalty Expired"),
    INVALID_PENALTY_AMOUNT(HttpStatus.BAD_REQUEST, "PN004", "Invalid Penalty Amount"),
    PENALTY_CANNOT_BE_CANCELLED(HttpStatus.BAD_REQUEST, "PN005", "Penalty Cannot Be Cancelled"),
    UNAUTHORIZED_PENALTY_ACCESS(HttpStatus.FORBIDDEN, "PN006", "Unauthorized Penalty Access"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}