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

export interface MemberVerificationCount {
  userId: number;
  userName: string;
  verificationCount: number;
}

export interface CreateVerificationRequest {
  imageUrl: string;
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

/**
 * 멤버별 주간 인증 횟수를 조회합니다.
 */
export const getMemberVerificationCounts = (challengeId: string) => {
  return apiFetch<MemberVerificationCount[]>(
    `/api/challenge-verifications/${challengeId}/member/verification-count`,
    { method: 'GET' }
  );
};

/**
 * 챌린지 인증을 생성합니다.
 * @param challengeId 챌린지 ID
 * @param userId 사용자 ID (임시, 로그인 후 제거)
 * @param request 인증 요청 데이터
 */
export const createVerification = (
  challengeId: string,
  userId: number,
  request: CreateVerificationRequest
) => {
  return apiFetch<ChallengeVerificationResponse>(
    `/api/challenge-verifications/${challengeId}?userId=${userId}`,
    {
      method: 'POST',
      body: JSON.stringify(request),
    }
  );
};
