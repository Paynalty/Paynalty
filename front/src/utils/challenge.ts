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
