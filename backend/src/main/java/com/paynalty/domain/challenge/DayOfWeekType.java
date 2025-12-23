package com.paynalty.domain.challenge;

import java.time.DayOfWeek;

/**
 * 요일 타입 (짧은 형식)
 */
public enum DayOfWeekType {
    MON("월요일", DayOfWeek.MONDAY),
    TUE("화요일", DayOfWeek.TUESDAY),
    WED("수요일", DayOfWeek.WEDNESDAY),
    THU("목요일", DayOfWeek.THURSDAY),
    FRI("금요일", DayOfWeek.FRIDAY),
    SAT("토요일", DayOfWeek.SATURDAY),
    SUN("일요일", DayOfWeek.SUNDAY);

    private final String koreanName;
    private final DayOfWeek dayOfWeek;

    DayOfWeekType(String koreanName, DayOfWeek dayOfWeek) {
        this.koreanName = koreanName;
        this.dayOfWeek = dayOfWeek;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * DayOfWeek를 DayOfWeekType으로 변환
     */
    public static DayOfWeekType from(DayOfWeek dayOfWeek) {
        for (DayOfWeekType type : values()) {
            if (type.dayOfWeek == dayOfWeek) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DayOfWeek: " + dayOfWeek);
    }

    /**
     * 오늘의 DayOfWeekType 반환
     */
    public static DayOfWeekType today() {
        return from(DayOfWeek.from(java.time.LocalDate.now()));
    }
}

