import { useQuery, queryOptions } from '@tanstack/react-query';
import { getMyProgressChallenges, ChallengeDetailResponse } from '../api/challenges';
import { Challenge } from '../components/challenge/types';
import { sortChallengesByPriority } from '../utils/challenge';
import { ApiError } from '../api/client';
import { useAuthStore } from '../stores/authStore';

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
  if (status === 'ACTIVE') {
    return sortChallengesByPriority(challenges);
  }

  return [...challenges].sort((a, b) => {
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
        try {
          const data = await getMyProgressChallenges(status);
          const mapped = data.map((item) => mapToChallenge(item, status));
          return sortChallenges(mapped, status);
        } catch (error) {
          if (error instanceof ApiError && error.status === 404) {
            return [];
          }
          throw error;
        }
      },
    }),
  missions: () =>
    queryOptions({
      queryKey: [...challengeQueries.all, 'missions'],
      queryFn: async () => {
        try {
          const data = await getMyProgressChallenges('ACTIVE');
          const mapped = data.map((item) => mapToChallenge(item, 'ACTIVE'));
          return sortChallenges(mapped, 'ACTIVE');
        } catch (error) {
          if (error instanceof ApiError && error.status === 404) {
            return [];
          }
          throw error;
        }
      },
    }),
};

export const useChallenges = (status: 'ACTIVE' | 'PENDING' | 'COMPLETE') => {
  const { isLoggedIn } = useAuthStore();
  return useQuery({
    ...challengeQueries.lists(status),
    enabled: isLoggedIn,
  });
};

export const useMissionChallenges = () => {
  const { isLoggedIn } = useAuthStore();
  return useQuery({
    ...challengeQueries.missions(),
    enabled: isLoggedIn,
  });
};
