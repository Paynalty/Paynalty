import { useQuery, queryOptions } from '@tanstack/react-query';
import { getMyProgressChallenges, ChallengeDetailResponse } from '../api/challenges';
import { Challenge } from '../components/challenge/types';

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

export const challengeQueries = {
  all: ['challenges'] as const,
  lists: (status: 'ACTIVE' | 'PENDING' | 'COMPLETE') =>
    queryOptions({
      queryKey: [...challengeQueries.all, status],
      queryFn: async () => {
        const data = await getMyProgressChallenges(1, status);
        return data.map((item) => mapToChallenge(item, status));
      },
    }),
  missions: () =>
    queryOptions({
      queryKey: [...challengeQueries.all, 'missions'],
      queryFn: async () => {
        const data = await getMyProgressChallenges(1, 'ACTIVE');
        return data.map((item) => mapToChallenge(item, 'ACTIVE'));
      },
    }),
};

export const useChallenges = (status: 'ACTIVE' | 'PENDING' | 'COMPLETE') => {
  return useQuery(challengeQueries.lists(status));
};

export const useMissionChallenges = () => {
  return useQuery(challengeQueries.missions());
};
