import { useQuery } from '@tanstack/react-query';
import { getLatestVerification, getVerifications } from '../api/verifications';

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
