package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChallengeVerificationErrorCode implements ErrorCode {
    // 인증 관련 에러 (VER001~VER099)
    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "VER001", "인증 데이터를 찾을 수 없습니다."),
    NO_VERIFICATION_DATA(HttpStatus.NOT_FOUND, "VER002", "해당 챌린지에 인증 데이터가 없습니다."),
    
    // 인증 권한 에러
    NOT_VERIFICATION_OWNER_FOR_UPDATE(HttpStatus.FORBIDDEN, "VER010", "본인의 인증만 수정할 수 있습니다."),
    NOT_VERIFICATION_OWNER_FOR_DELETE(HttpStatus.FORBIDDEN, "VER011", "본인의 인증만 삭제할 수 있습니다."),
    
    // 인증 생성 제한 에러
    CHALLENGE_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "VER020", "인증 가능한 챌린지가 아닙니다."),
    OUTSIDE_VERIFICATION_TIME(HttpStatus.BAD_REQUEST, "VER021", "인증 가능 시간이 아닙니다."),
    NOT_ALLOWED_DAY(HttpStatus.BAD_REQUEST, "VER022", "오늘은 인증 가능한 요일이 아닙니다."),
    ALREADY_VERIFIED_TODAY(HttpStatus.BAD_REQUEST, "VER023", "오늘 이미 인증을 완료했습니다. (1일 1회만 가능)"),
    WEEKLY_FREQUENCY_EXCEEDED(HttpStatus.BAD_REQUEST, "VER024", "주간 인증 횟수를 초과했습니다."),
    
    // 인증 수정 시간 제한 에러
    CANNOT_UPDATE_PAST_VERIFICATION(HttpStatus.BAD_REQUEST, "VER030", "당일 인증만 수정할 수 있습니다."),
    CANNOT_UPDATE_OUTSIDE_TIME_RANGE(HttpStatus.BAD_REQUEST, "VER031", "인증 가능 시간대에만 수정할 수 있습니다."),
    CANNOT_UPDATE_OUTSIDE_ALLOWED_DAYS(HttpStatus.BAD_REQUEST, "VER032", "인증 가능한 요일에만 수정할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

