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
          id: String(item.id),
          title: item.title,
          verificationStatus: item.verificationStatus,
          weeklyProgressCount: item.weeklyProgressCount ?? 0,
          weeklyRequiredCount: String(item.weeklyRequiredCount),
          penaltyAmount: Number(item.penaltyAmount),
          verifyStart: item.verifyStart || '',
          verifyEnd: item.verifyEnd || '',
          daysOfWeek: item.daysOfWeek ?? [],
          endAt: item.endAt,
          participantCount: 2, // 임시 데이터
          participants: '기영, 호영', // 임시 데이터
          verificationType: item.verificationType || '',
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
          id: String(item.id),
          title: item.title,
          verificationStatus: item.verificationStatus,
          weeklyProgressCount: item.weeklyProgressCount ?? 0,
          weeklyRequiredCount: String(item.weeklyRequiredCount),
          penaltyAmount: Number(item.penaltyAmount),
          verifyStart: item.verifyStart || '',
          verifyEnd: item.verifyEnd || '',
          daysOfWeek: item.daysOfWeek ?? [],
          endAt: item.endAt,
          participants: '기영, 호영', // 임시
          participantCount: 2, // 임시
          verificationType: item.verificationType || '',
        })) as Challenge[];
      }
      return [];
    },
  });
};
