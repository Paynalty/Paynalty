import { useQuery } from '@tanstack/react-query';
import { getLatestVerification, getVerifications } from '../api/verifications';

export const useLatestVerification = (challengeId: string) => {
  return useQuery({
    queryKey: ['latestVerification', challengeId],
    queryFn: async () => {
      if (!challengeId) return null;
      const response = await getLatestVerification(challengeId);
      if (response.success && response.data) {
        return {
          id: response.data.id,
          name: response.data.userName,
          // avatar: response.data.userAvatar,
          image: response.data.imageUrl,
          dateTime: response.data.dateTime,
        };
      }
      return null;
    },
    enabled: !!challengeId,
  });
};

export const useVerifications = (challengeId: string) => {
  return useQuery({
    queryKey: ['verifications', challengeId],
    queryFn: async () => {
      if (!challengeId) return [];
      const response = await getVerifications(challengeId);
      if (response.success && response.data) {
        return response.data.content;
      }
      return [];
    },
    enabled: !!challengeId,
  });
};
