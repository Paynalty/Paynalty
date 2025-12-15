package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    // 결제 관련 에러 (PAY001~PAY099)
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY001", "Payment Not Found"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAY002", "Payment Failed"),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.CONFLICT, "PAY003", "Payment Already Completed"),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "PAY004", "Invalid Payment Amount"),
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_GATEWAY, "PAY005", "Payment Gateway Error"),
    PAYMENT_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "PAY006", "Payment Timeout"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "PAY007", "Insufficient Balance"),
    PAYMENT_CANCELLED(HttpStatus.BAD_REQUEST, "PAY008", "Payment Cancelled"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}