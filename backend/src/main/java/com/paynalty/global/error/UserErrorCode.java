package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    // 사용자 관련 에러 (U001~U099)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User Not Found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "User Already Exists"),
    INVALID_USER_CREDENTIALS(HttpStatus.UNAUTHORIZED, "U003", "Invalid User Credentials"),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U004", "User Unauthorized"),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "U005", "User Forbidden"),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "U006", "Invalid User Status"),
    
    // 사용자 검색 에러
    SEARCH_KEYWORD_EMPTY(HttpStatus.BAD_REQUEST, "U010", "검색어를 입력해주세요."),
    SEARCH_KEYWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "U011", "검색어는 최소 2자 이상이어야 합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}