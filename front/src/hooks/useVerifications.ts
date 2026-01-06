import { useInfiniteQuery, useQuery } from '@tanstack/react-query';
import { getLatestVerification, getVerifications, getMemberVerificationCounts } from '../api/verifications';

export const useLatestVerification = (challengeId: string) => {
  return useQuery({
    queryKey: ['latestVerification', challengeId],
    queryFn: async () => {
      if (!challengeId) return null;
      const data = await getLatestVerification(challengeId);
      return {
        id: data.id,
        name: data.userName,
        // avatar: response.data.userAvatar,
        image: data.imageUrl,
        dateTime: data.dateTime,
      };
    },
    enabled: !!challengeId,
  });
};

export const useVerifications = (challengeId: string) => {
  return useInfiniteQuery({
    queryKey: ['verifications', challengeId],
    queryFn: async ({ pageParam = 0 }) => {
      if (!challengeId) return { content: [], hasNext: false, number: 0, size: 5 };
      return await getVerifications(challengeId, pageParam as number, 5);
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.hasNext) {
        return lastPage.number + 1;
      }
      return undefined;
    },
    initialPageParam: 0,
    enabled: !!challengeId,
  });
};

/**
 * 멤버별 주간 인증 횟수를 조회하는 Hook
 */
export const useMemberVerificationCounts = (challengeId: string) => {
  return useQuery({
    queryKey: ['memberVerificationCounts', challengeId],
    queryFn: async () => {
      if (!challengeId) return [];
      return await getMemberVerificationCounts(challengeId);
    },
    enabled: !!challengeId,
  });
};
