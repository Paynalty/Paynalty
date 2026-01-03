import { useQuery, queryOptions } from '@tanstack/react-query';
import { getMyProgressChallenges, ChallengeDetailResponse } from '../api/challenges';
import { Challenge } from '../components/challenge/types';
import { isTodayChallenge, getTimeDate } from '../utils/challenge';

/**
 * API 응답 데이터를 UI용 Challenge 객체로 변환합니다.
 */
const mapToChallenge = (item: ChallengeDetailResponse, status?: 'ACTIVE' | 'PENDING' | 'COMPLETE'): Challenge => ({
  ...item,
  id: String(item.id),
  weeklyRequiredCount: String(item.weeklyRequiredCount),
  participantCount: 2, // Mock
  participants: '기영, 호영', // Mock
  status,
});

/**
 * 상태별 정렬 로직을 적용합니다.
 */
const sortChallenges = (challenges: Challenge[], status: 'ACTIVE' | 'PENDING' | 'COMPLETE') => {
  return [...challenges].sort((a, b) => {
    if (status === 'ACTIVE') {
      const aToday = isTodayChallenge(a.daysOfWeek, Number(a.weeklyRequiredCount), a.weeklyProgressCount);
      const bToday = isTodayChallenge(b.daysOfWeek, Number(b.weeklyRequiredCount), b.weeklyProgressCount);

      const aNeedVerify = aToday && a.verificationStatus !== 'VERIFIED';
      const bNeedVerify = bToday && b.verificationStatus !== 'VERIFIED';

      // 1. 오늘 인증이 필요한데 아직 안 한 것이 최우선
      if (aNeedVerify && !bNeedVerify) return -1;
      if (!aNeedVerify && bNeedVerify) return 1;

      // 2. 오늘 인증일인 것이 그 다음 우선 (이미 완료했더라도 위에서 걸러지지 않았다면 여기로 옴)
      if (aToday && !bToday) return -1;
      if (!aToday && bToday) return 1;

      // 3. 인증 시간이 얼마 안 남은 순 (verifyEnd 기준)
      if (a.verifyEnd && b.verifyEnd) {
        const aEnd = getTimeDate(a.verifyEnd).getTime();
        const bEnd = getTimeDate(b.verifyEnd).getTime();
        return aEnd - bEnd;
      }
      return 0;
    }

    if (status === 'PENDING') {
      // 시작 예정 순 (시작 날짜가 빠른 순)
      const aStart = a.startAt ? new Date(a.startAt).getTime() : Infinity;
      const bStart = b.startAt ? new Date(b.startAt).getTime() : Infinity;
      return aStart - bStart;
    }

    if (status === 'COMPLETE') {
      // 최근 완료일 순 (종료 날짜가 늦은 순 - 내림차순)
      const aEnd = a.endAt ? new Date(a.endAt).getTime() : 0;
      const bEnd = b.endAt ? new Date(b.endAt).getTime() : 0;
      return bEnd - aEnd;
    }

    return 0;
  });
};

export const challengeQueries = {
  all: ['challenges'] as const,
  lists: (status: 'ACTIVE' | 'PENDING' | 'COMPLETE') =>
    queryOptions({
      queryKey: [...challengeQueries.all, status],
      queryFn: async () => {
        const data = await getMyProgressChallenges(1, status);
        const mapped = data.map((item) => mapToChallenge(item, status));
        return sortChallenges(mapped, status);
      },
    }),
  missions: () =>
    queryOptions({
      queryKey: [...challengeQueries.all, 'missions'],
      queryFn: async () => {
        const data = await getMyProgressChallenges(1, 'ACTIVE');
        const mapped = data.map((item) => mapToChallenge(item, 'ACTIVE'));
        return sortChallenges(mapped, 'ACTIVE');
      },
    }),
};

export const useChallenges = (status: 'ACTIVE' | 'PENDING' | 'COMPLETE') => {
  return useQuery(challengeQueries.lists(status));
};

export const useMissionChallenges = () => {
  return useQuery(challengeQueries.missions());
};
