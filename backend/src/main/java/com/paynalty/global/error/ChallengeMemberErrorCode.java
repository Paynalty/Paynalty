package com.paynalty.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChallengeMemberErrorCode implements ErrorCode {
    // 챌린지 멤버 관련 에러 (MEM001~MEM099)
    CHALLENGE_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM001", "챌린지 멤버를 찾을 수 없습니다."),
    NOT_CHALLENGE_MEMBER(HttpStatus.FORBIDDEN, "MEM002", "챌린지에 참여하지 않은 사용자입니다."),

    // 챌린지 멤버 관리 권한 에러
    NOT_CREATOR(HttpStatus.FORBIDDEN, "MEM003", "챌린지 생성자만 멤버를 관리할 수 있습니다."),
    CREATOR_CANNOT_BE_REMOVED(HttpStatus.BAD_REQUEST, "MEM004", "챌린지 생성자는 자신을 제외할 수 없습니다."),
    MEMBER_HAS_UNPAID_PENALTY(HttpStatus.BAD_REQUEST, "MEM005", "미납된 패널티가 있어 탈퇴할 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}

