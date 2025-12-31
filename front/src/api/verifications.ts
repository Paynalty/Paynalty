import { apiFetch } from './client';
import { ApiResponse } from './challenges';

export interface ChallengeVerificationResponse {
  id: number;
  userName: string;
  // userAvatar: string;
  imageUrl: string;
  dateTime: string;
}

/**
 * 특정 챌린지의 가장 최근 인증 내역을 조회합니다.
 */
export const getLatestVerification = (challengeId: string) => {
  return apiFetch<ApiResponse<ChallengeVerificationResponse>>(`/api/challenge-verifications/${challengeId}/latest`, {
    method: 'GET',
  });
};
