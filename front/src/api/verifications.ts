import { apiFetch } from './client';

export interface SliceResponse<T> {
  content: T[];
  hasNext: boolean;
  number: number;
  size: number;
}

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
  return apiFetch<ChallengeVerificationResponse>(`/api/challenge-verifications/${challengeId}/latest`, {
    method: 'GET',
  });
};

/**
 * 특정 챌린지의 인증 내역 목록을 조회합니다 (페이징 지원).
 */
export const getVerifications = (challengeId: string, page: number = 0, size: number = 5) => {
  return apiFetch<SliceResponse<ChallengeVerificationResponse>>(
    `/api/challenge-verifications/${challengeId}/list?page=${page}&size=${size}`,
    {
      method: 'GET',
    }
  );
};
