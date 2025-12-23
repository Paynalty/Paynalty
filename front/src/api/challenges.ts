import { apiFetch } from './client';

export type VerificationType = 'PHOTO' | 'TEXT' | 'VOTE';
export type DayOfWeekType = 'MON' | 'TUE' | 'WED' | 'THU' | 'FRI' | 'SAT' | 'SUN';

export interface CreateChallengeRequest {
  title: string;
  startDate: string; // ISO date string (YYYY-MM-DD)
  endDate: string; // ISO date string (YYYY-MM-DD)
  frequency?: number; // 주 n회 인증
  dayOfWeek?: DayOfWeekType[]; // 지정 요일 (선택사항)
  penaltyAmount: number;
  verifyStartAt?: string; // HH:mm 형식
  verifyEndAt?: string; // HH:mm 형식
  verificationType: VerificationType;
}

export interface CreateChallengeResponse {
  id: number;
  title: string;
  status: string;
}

export const createChallenge = (data: CreateChallengeRequest) => {
  return apiFetch<CreateChallengeResponse>('/challenges', {
    method: 'POST',
    body: JSON.stringify(data),
  });
};
