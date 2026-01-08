import { z } from 'zod';
import { apiFetch } from './client';
import { UserResponseSchema } from './users';

// 멤버 업데이트 요청 스키마
export const ChallengeMemberUpdateRequestSchema = z.object({
  tossIds: z.array(z.number()),
});

// 멤버 응답 스키마 (UserResponse와 유사하지만 role 등의 필드 추가 가능)
export const ChallengeMemberResponseSchema = z.object({
  id: z.number(),
  tossId: z.number(),
  userName: z.string(),
  challengeId: z.number(),
  joinedAt: z.string().optional(),
  isSuccess: z.string().optional(),
  endAt: z.string().optional(),
  role: z.enum(['CREATOR', 'CHALLENGER']),
});
export type ChallengeMemberResponse = z.infer<typeof ChallengeMemberResponseSchema>;

/**
 * 챌린지 멤버 목록을 수정합니다 (생성자 전용).
 */
export const updateChallengeMembers = (challengeId: number, tossIds: number[]) => {
  return apiFetch<void>(`/api/challenge-members/${challengeId}/members`, {
    method: 'PUT',
    body: JSON.stringify({ tossIds }),
    schema: z.void(),
  });
};

/**
 * 챌린지에서 탈퇴합니다 (참여자 전용).
 */
export const leaveChallenge = (challengeId: number) => {
  return apiFetch<void>(`/api/challenge-members/${challengeId}/me`, {
    method: 'DELETE',
    schema: z.void(),
  });
};

/**
 * 챌린지 멤버 목록을 조회합니다.
 */
export const getChallengeMembers = (challengeId: number) => {
  return apiFetch<ChallengeMemberResponse[]>(`/api/challenge-members/${challengeId}`, {
    method: 'GET',
    schema: z.array(ChallengeMemberResponseSchema),
  });
};
