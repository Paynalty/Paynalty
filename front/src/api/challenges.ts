import { apiFetch } from './client';

export type VerificationType = 'PHOTO' | 'TEXT' | 'VOTE';
export type DayOfWeekType = 'MON' | 'TUE' | 'WED' | 'THU' | 'FRI' | 'SAT' | 'SUN';

export interface CreateChallengeRequest {
  title: string;
  startDate: string;
  endDate: string;
  frequency?: number; // 주 n회 인증
  dayOfWeek?: DayOfWeekType[]; // 지정 요일 (선택사항)
  penaltyAmount: number;
  verifyStartAt?: string;
  verifyEndAt?: string;
  verificationType: VerificationType;
  userIds?: number[];
}

export interface CreateChallengeResponse {
  id: number;
  title: string;
  status: string;
}

export interface ChallengeDetailResponse {
  verificationStatus: string;
  id: number;
  title: string;
  weeklyProgressCount: number;
  weeklyRequiredCount: number;
  penaltyAmount: number;
  verifyStart: string;
  verifyEnd: string;
  verificationType: string;
  daysOfWeek: string[];
  endAt: string;
}

export const createChallenge = (data: CreateChallengeRequest) => {
  return apiFetch<CreateChallengeResponse>('/api/challenge', {
    method: 'POST',
    body: JSON.stringify(data),
  });
};

export const getMyProgressChallenges = (userId: number = 1, status: 'PENDING' | 'ACTIVE' | 'COMPLETE' = 'ACTIVE') => {
  return apiFetch<ChallengeDetailResponse[]>(`/api/challenge/${userId}/${status}`, {
    method: 'GET',
  });
};
