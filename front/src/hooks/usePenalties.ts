import { useQuery } from '@tanstack/react-query';
import { getAllPenalties, getMyPenalties } from '../api/penalties';
import { useAuthStore } from '../stores/authStore';

export const useMyPenalties = (challengeId: number | null, options?: { enabled?: boolean }) => {
  const { isLoggedIn } = useAuthStore();
  return useQuery({
    queryKey: ['penalties', 'me', challengeId],
    queryFn: async () => {
      if (!challengeId) return [];
      return await getMyPenalties(challengeId);
    },
    enabled: !!challengeId && isLoggedIn && (options?.enabled ?? true),
  });
};

export const useAllPenalties = (challengeId: number | null, options?: { enabled?: boolean }) => {
  const { isLoggedIn } = useAuthStore();
  return useQuery({
    queryKey: ['penalties', 'all', challengeId],
    queryFn: async () => {
      if (!challengeId) return [];
      return await getAllPenalties(challengeId);
    },
    enabled: !!challengeId && isLoggedIn && (options?.enabled ?? true),
  });
};
