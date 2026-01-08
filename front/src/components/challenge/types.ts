import { ChallengeResponse } from '../../api/challenges';

export type verificationStatus = 'VERIFIED' | 'NOT_VERIFIED';

export interface Challenge extends ChallengeResponse {
  // status?: 'ACTIVE' | 'PENDING' | 'COMPLETE'; // Removed (Now in ChallengeResponse)
}
