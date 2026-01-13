package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChallengeErrorCode implements ErrorCode {
    // 챌린지 관련 에러 (CHA001~CHA099)
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHA001", "챌린지를 찾을 수 없습니다."),
    
    // 챌린지 생성 시 유효성 검증 에러
    INVALID_FREQUENCY(HttpStatus.BAD_REQUEST, "CHA010", "dayOfWeeks가 없을 때는 frequency 값(양수)이 필수입니다."),
    INVALID_START_DATE_PAST(HttpStatus.BAD_REQUEST, "CHA011", "시작일은 이미 지난 날짜일 수 없습니다."),
    INVALID_END_DATE_PAST(HttpStatus.BAD_REQUEST, "CHA013", "마감일은 이미 지난 날짜일 수 없습니다."),
    INVALID_END_DATE_TODAY(HttpStatus.BAD_REQUEST, "CHA014", "마감일은 오늘 날짜일 수 없습니다. 최소 내일 이후로 설정해주세요."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "CHA015", "마감일은 시작일보다 이후여야 합니다."),
    INVALID_VERIFICATION_TIME(HttpStatus.BAD_REQUEST, "CHA016", "인증 시작 시간은 마감 시간보다 이전이어야 합니다."),
    
    // 챌린지 권한 에러
    NOT_CHALLENGE_CREATOR_FOR_UPDATE(HttpStatus.FORBIDDEN, "CHA020", "챌린지 생성자만 수정할 수 있습니다."),
    NOT_CHALLENGE_CREATOR_FOR_DELETE(HttpStatus.FORBIDDEN, "CHA021", "챌린지 생성자만 삭제할 수 있습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

