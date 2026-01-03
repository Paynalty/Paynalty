import { ChallengeDetailResponse } from '../../api/challenges';

export type verificationStatus = 'VERIFIED' | 'NOT_VERIFIED';

export interface Challenge extends Omit<ChallengeDetailResponse, 'id' | 'weeklyRequiredCount'> {
  id: string; // UI 호환성을 위해 string 유지
  weeklyRequiredCount: string; // UI 호환성을 위해 string 유지
  participants: string;
  participantCount: number;
  status?: 'ACTIVE' | 'PENDING' | 'COMPLETE';
}
