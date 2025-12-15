package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    // 500번대 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "Internal Server Error"),

    // 400번대 클라이언트 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G002", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "G003", "Method Not Allowed"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G004", "Invalid Type Value"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "G005", "Missing Request Parameter"),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "G006", "Invalid Request Body"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "G007", "Entity Not Found"),

    // 인증/인가 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "G008", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "G009", "Forbidden"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
