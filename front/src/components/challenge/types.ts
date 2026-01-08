import { ChallengeDetailResponse } from '../../api/challenges';

export type verificationStatus = 'VERIFIED' | 'NOT_VERIFIED';

export interface Challenge extends ChallengeDetailResponse {
  participants: string;
  participantCount: number;
  status?: 'ACTIVE' | 'PENDING' | 'COMPLETE';
}
