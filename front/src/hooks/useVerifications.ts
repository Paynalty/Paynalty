import {useInfiniteQuery, useQuery} from '@tanstack/react-query';
import {getLatestVerification, getMemberVerificationCounts, getVerifications} from '../api/verifications';
import {ApiError} from '../api/client';

export const useLatestVerification = (challengeId: number | null) => {
  return useQuery({
    queryKey: ['latestVerification', challengeId],
    queryFn: async () => {
      if (!challengeId) return null;
      try {
        const data = await getLatestVerification(challengeId);
        return {
          id: data.id,
          tossId: data.tossId,
          name: data.userName,
          // avatar: response.data.userAvatar,
          image: data.imageUrl,
          dateTime: data.dateTime,
        };
      } catch (error: any) {
        // 인증 데이터가 없는 경우(404) 에러를 던지지 않고 null 반환
        if (error instanceof ApiError && error.status === 404) {
          return null;
        }
        throw error;
      }
    },
    enabled: !!challengeId,
  });
};

export const useVerifications = (challengeId: number | null) => {
  return useInfiniteQuery({
    queryKey: ['verifications', challengeId],
    queryFn: async ({ pageParam = 0 }) => {
      if (!challengeId) return { content: [], hasNext: false, number: 0, size: 5 };
      try {
        return await getVerifications(challengeId, pageParam as number, 5);
      } catch (error: any) {
        if (error instanceof ApiError && error.status === 404) {
          return { content: [], hasNext: false, number: 0, size: 5 };
        }
        throw error;
      }
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
export const useMemberVerificationCounts = (challengeId: number | null) => {
  return useQuery({
    queryKey: ['memberVerificationCounts', challengeId],
    queryFn: async () => {
      if (!challengeId) return [];
      try {
        return await getMemberVerificationCounts(challengeId);
      } catch (error: any) {
        if (error instanceof ApiError && error.status === 404) {
          return [];
        }
        throw error;
      }
    },
    enabled: !!challengeId,
  });
};
