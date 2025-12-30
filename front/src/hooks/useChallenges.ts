import { useQuery } from '@tanstack/react-query';
import { getMyProgressChallenges } from '../api/challenges';
import { Challenge } from '../components/challenge/types';

export const useChallenges = (status: 'ACTIVE' | 'PENDING' | 'COMPLETE') => {
  return useQuery({
    queryKey: ['challenges', status],
    queryFn: async () => {
      const response = await getMyProgressChallenges(1, status);
      if (response.success) {
        return response.data.map((item) => ({
          challengeId: String(item.challengeId),
          title: item.title,
          status: status as any,
          currentCount: item.currentWeeklyVerificationCount ?? 0,
          penaltyAmount: Number(item.penaltyAmount),
          participants: '기영, 호영',
          participantCount: 2,
          verifyStartAt: item.verifyStartAt || '',
          verifyEndAt: item.verifyEndAt || '',
          verificationFrequency: String(item.frequency),
          verificationType: item.verificationType || 'PHOTO',
        })) as Challenge[];
      }
      return [];
    },
  });
};

export const useMissionChallenges = () => {
  return useQuery({
    queryKey: ['missionChallenges'],
    queryFn: async () => {
      const response = await getMyProgressChallenges(1, 'ACTIVE');
      if (response.success) {
        return response.data.map((item) => ({
          challengeId: String(item.challengeId),
          title: item.title,
          status: 'ACTIVE' as any,
          currentCount: item.currentWeeklyVerificationCount ?? 0,
          penaltyAmount: Number(item.penaltyAmount),
          participants: '기영, 호영',
          participantCount: 2,
          verifyStartAt: item.verifyStartAt || '',
          verifyEndAt: item.verifyEndAt || '',
          verificationFrequency: String(item.frequency),
          verificationType: item.verificationType || 'PHOTO',
        })) as Challenge[];
      }
      return [];
    },
  });
};
