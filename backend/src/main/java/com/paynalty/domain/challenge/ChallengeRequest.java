package com.paynalty.domain.challenge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "챌린지 생성 요청")
public class ChallengeRequest {

    @Schema(description = "챌린지명 (필수, 최대 50자)", example = "매일 운동하기", required = true)
    @NotBlank(message = "챌린지명은 필수입니다")
    @Size(max = 50, message = "챌린지명은 50자 이하여야 합니다")
    private String title;

    @Schema(
            description = "시작 옵션 (필수)\n" +
                    "- \"tomorrow\": 내일 00:00부터 시작\n" +
                    "- \"nextWeek\": 다음주 월요일 00:00부터 시작\n" +
                    "※ 대소문자 구분 없음 (tomorrow, Tomorrow, TOMORROW 모두 가능)",
            example = "tomorrow",
            required = true,
            allowableValues = {"tomorrow", "nextWeek"}
    )
    @NotBlank(message = "시작 옵션은 필수입니다")
    private String startOption;

    @Schema(
            description = "챌린지 종료 날짜 (필수)\n" +
                    "- 형식: YYYY-MM-DD (예: 2024-12-31)\n" +
                    "- 오늘 날짜보다 이후여야 함 (오늘 포함 불가)\n" +
                    "- 시작일보다 이후여야 함",
            example = "2025-12-31",
            required = true
    )
    @NotNull(message = "종료 날짜는 필수입니다")
    private LocalDate endDate;

    @Schema(
            description = "인증 주기 (주에 몇 번 인증할지)\n" +
                    "⚠️ designatedDays와 frequency는 둘 중 하나만 사용 가능\n" +
                    "- designatedDays가 null이거나 빈 배열일 때만 사용\n" +
                    "- designatedDays가 있으면 이 값은 무시되고 자동 계산됨\n" +
                    "- 예: 4 (요일 상관없이 주에 4회 인증)",
            example = "null"
    )
    private Integer frequency;

    @Schema(
            description = "지정된 요일 목록\n" +
                    "⚠️ designatedDays와 frequency는 둘 중 하나만 사용 가능\n" +
                    "- 이 값이 있으면 frequency는 자동으로 요일 개수로 계산됨\n" +
                    "- 예: [\"월\", \"수\", \"목\", \"토\"] → frequency = 4 (자동 계산)\n" +
                    "- null이거나 빈 배열이면 frequency 값을 사용\n" +
                    "- 가능한 요일 값: \"월\", \"화\", \"수\", \"목\", \"금\", \"토\", \"일\"",
            example = "[\"월\", \"수\", \"목\", \"토\"]"
    )
    private List<String> designatedDays;

    @Schema(description = "벌금 금액 (필수, 양수)", example = "10000", required = true)
    @NotNull(message = "패널티 금액은 필수입니다")
    private Long penaltyAmount;

    @Schema(
            description = "인증 가능 시작 시간 (선택)\n" +
                    "- 형식: HH:mm (예: 06:00)\n" +
                    "- null이면 기본값 00:00 사용\n" +
                    "- verifyEndAt보다 이전이어야 함",
            example = "00:00"
    )
    private LocalTime verifyStartAt;

    @Schema(
            description = "인증 가능 종료 시간 (선택)\n" +
                    "- 형식: HH:mm (예: 23:59)\n" +
                    "- null이면 기본값 23:59 사용\n" +
                    "- verifyStartAt보다 이후여야 함",
            example = "23:59"
    )
    private LocalTime verifyEndAt;

    @Schema(
            description = "인증 방식 (필수)\n" +
                    "- PHOTO: 사진 인증 (이미지 URL)\n" +
                    "- TEXT: 텍스트 인증\n" +
                    "- CHECKBOX: 체크박스 인증",
            example = "PHOTO",
            required = true,
            allowableValues = {"PHOTO", "TEXT", "CHECKBOX"}
    )
    @NotNull(message = "인증 방식은 필수입니다")
    private VerificationType verificationType;

}

