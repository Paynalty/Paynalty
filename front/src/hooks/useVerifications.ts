import { useQuery } from '@tanstack/react-query';
import { getLatestVerification } from '../api/verifications';
import { formatDate, formatTime } from '../utils/challenge';

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
