import { useQuery } from '@tanstack/react-query';
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
  return useQuery({
    queryKey: ['verifications', challengeId],
    queryFn: async () => {
      if (!challengeId) return [];
      const data = await getVerifications(challengeId);
      return data.content;
    },
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
