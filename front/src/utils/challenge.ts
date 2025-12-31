import { verificationStatus } from '../components/challenge/types';

/**
 * "HH:mm:ss" 형식의 시간 문자열을 받아 오늘 날짜의 Date 객체로 변환합니다.
 * 백엔드에서 LocalTime이나 LocalDateTime(ISO string) 어떤 것을 보내도 처리할 수 있습니다.
 */
export const getTimeDate = (timeString: string) => {
  if (!timeString) return new Date();

  // 날짜까지 포함된 ISO string인 경우
  if (timeString.includes('T') || timeString.includes('-')) {
    return new Date(timeString);
  }

  // 시간만 있는 경우 (LocalTime)
  const parts = timeString.split(':').map(Number);
  const hours = parts[0] ?? 0;
  const minutes = parts[1] ?? 0;

  const date = new Date();
  date.setHours(hours, minutes, 0, 0);
  return date;
};

/**
 * 인증 시작 시간과 종료 시간을 받아 현재 상태에 맞는 메시지를 반환합니다.
 */
export const getVerificationMessage = (verifyStart?: string, verifyEnd?: string): string => {
  if (!verifyStart || !verifyEnd) return '시간 정보 없음';

  const start = getTimeDate(verifyStart).getTime();
  const end = getTimeDate(verifyEnd).getTime();
  const now = Date.now();

  if (isNaN(start) || isNaN(end)) return '시간 정보 없음';

  if (now < start) {
    // 인증 시작 전: "대기 : X시간 Y분 후 인증 가능"
    const diff = start - now;
    const h = Math.floor(diff / 3600000);
    const m = Math.floor((diff % 3600000) / 60000);
    if (h > 0) return `대기 : ${h}시간 ${m}분 후 가능`;
    return `대기 : ${m}분 후 인증 가능`;
  } else if (now <= end) {
    // 인증 중: "인증 중 : X시간 Y분 남음"
    const diff = end - now;
    const h = Math.floor(diff / 3600000);
    const m = Math.floor((diff % 3600000) / 60000);
    return `인증 중 : ${h}시간 ${m}분 남음`;
  } else {
    // 인증 종료

    return '오늘 인증을 못했어요';
  }
};

// 요일 매핑 (Backend String -> JS Date.getDay())
const DAYS_MAP: { [key: string]: number } = {
  SUN: 0,
  MON: 1,
  TUE: 2,
  WED: 3,
  THU: 4,
  FRI: 5,
  SAT: 6,
};

// JS Date.getDay() -> 한글 요일
const DAYS_LABEL: { [key: number]: string } = {
  0: '일',
  1: '월',
  2: '화',
  3: '수',
  4: '목',
  5: '금',
  6: '토',
};

/**
 * 오늘 수행해야 할 미션인지 확인합니다.
 * @param daysOfWeek 인증 요일 목록 (['MON', 'WED']...)
 * @param weeklyRequiredCount 주간 필수 인증 횟수
 * @param weeklyProgressCount 현재 주간 인증 횟수
 */
export const isTodayChallenge = (
  daysOfWeek: string[] | undefined,
  weeklyRequiredCount: number,
  weeklyProgressCount: number
): boolean => {
  // 인증 요일이 정해져 있는 경우
  if (daysOfWeek && daysOfWeek.length > 0) {
    const today = new Date().getDay();
    return daysOfWeek.some((day) => DAYS_MAP[day] === today);
  }

  // 인증 요일이 없는 경우 (자율) -> 횟수가 남았으면 오늘 할 수 있음
  return weeklyProgressCount < weeklyRequiredCount;
};

/**
 * 인증 요일이 아닐 때, 다음 인증 가능한 요일을 안내하는 메시지를 반환합니다.
 */
export const getNextScheduleMessage = (daysOfWeek: string[] | undefined): string => {
  if (!daysOfWeek || daysOfWeek.length === 0) return '자율 인증 가능';

  const today = new Date().getDay();
  // 오늘의 요일 숫자 리스트로 변환 및 정렬
  const scheduleDays = daysOfWeek
    .map((day) => DAYS_MAP[day])
    .filter((d): d is number => d !== undefined)
    .sort((a, b) => a - b);

  // 오늘 이후의 가장 가까운 요일 찾기
  let nextDay = scheduleDays.find((day) => day > today);

  // 오늘 이후에 없으면, 다음 주의 첫 번째 요일이 다음 인증일
  if (nextDay === undefined) {
    nextDay = scheduleDays[0];
  }

  if (nextDay === undefined) return ''; // 예외 케이스

  return `다음 인증일 : ${DAYS_LABEL[nextDay]}요일`;
};

/**
 * 챌린지 상태에 따른 뱃지(라벨, 색상) 정보를 반환합니다.
 */
export const getChallengeStatusBadge = (
  status: verificationStatus,
  daysOfWeek: string[],
  weeklyRequiredCount: number,
  weeklyProgressCount: number
) => {
  // 1. 이미 인증을 완료한 경우 -> Green
  if (status === 'VERIFIED') {
    return { label: '인증 완료', type: 'green' as const, style: 'weak' as const };
  }

  // 2. 오늘 인증해야 하는 경우 (isTodayChallenge 활용) -> Yellow
  if (isTodayChallenge(daysOfWeek, weeklyRequiredCount, weeklyProgressCount)) {
    return { label: '지금 할 차례에요', type: 'yellow' as const, style: 'weak' as const };
  }

  // 3. 그 외 -> Blue
  return { label: '대기중', type: 'blue' as const, style: 'weak' as const };
};

/**
 * ISO 날짜 문자열 또는 시간 문자열을 받아 "M월 D일" 형식으로 변환합니다.
 */
export const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = getTimeDate(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

/**
 * ISO 날짜 문자열 또는 시간 문자열을 받아 "오전/오후 HH:mm" 형식으로 변환합니다.
 */
export const formatTime = (dateString: string) => {
    if (!dateString) return '';
    const date = getTimeDate(dateString);
    return date.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true,
    });
};

export const formatDaysOfWeek = (days: string[] | undefined) => {
  if (!days || days.length === 0) return '';
  return days
    .map((day) => {
      const dayNum = DAYS_MAP[day];
      return dayNum !== undefined ? DAYS_LABEL[dayNum] : day;
    })
    .join(', ');
};

/**
 * 영문 인증 방식을 한글 명칭으로 변환합니다.
 */
export const getVerificationTypeLabel = (type: string) => {
  const typeMap: { [key: string]: string } = {
    PHOTO: '사진',
    TEXT: '텍스트',
    VOTE: '투표',
  };
  return typeMap[type] || type;
};
