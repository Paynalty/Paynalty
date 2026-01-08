import { z } from 'zod';
import { apiFetch } from './client';

export const SliceResponseSchema = <T extends z.ZodTypeAny>(itemSchema: T) =>
  z.preprocess(
    (val: any) => {
      if (val && typeof val.last === 'boolean' && val.hasNext === undefined) {
        return { ...val, hasNext: !val.last };
      }
      return val;
    },
    z.object({
      content: z.array(itemSchema),
      hasNext: z.boolean(),
      number: z.number(),
      size: z.number(),
    })
  );

export const ChallengeVerificationResponseSchema = z.object({
  id: z.number(),
  userId: z.number(),
  userName: z.string(),
  imageUrl: z.string(),
  dateTime: z.string(),
});
export type ChallengeVerificationResponse = z.infer<typeof ChallengeVerificationResponseSchema>;

export const MemberVerificationCountSchema = z.object({
  tossId: z.number(),
  userName: z.string(),
  verificationCount: z.number(),
});
export type MemberVerificationCount = z.infer<typeof MemberVerificationCountSchema>;

export const CreateVerificationRequestSchema = z.object({
  imageUrl: z.string(),
});
export type CreateVerificationRequest = z.infer<typeof CreateVerificationRequestSchema>;

/**
 * 특정 챌린지의 가장 최근 인증 내역을 조회합니다.
 */
export const getLatestVerification = (challengeId: number) => {
  return apiFetch<ChallengeVerificationResponse>(`/api/challenge-verifications/${challengeId}/latest`, {
    method: 'GET',
    schema: ChallengeVerificationResponseSchema,
  });
};

/**
 * 특정 챌린지의 인증 내역 목록을 조회합니다 (페이징 지원).
 */
export const getVerifications = (challengeId: number, page: number = 0, size: number = 5) => {
  return apiFetch<any>(`/api/challenge-verifications/${challengeId}/list?page=${page}&size=${size}`, {
    method: 'GET',
    schema: SliceResponseSchema(ChallengeVerificationResponseSchema),
  });
};

/**
 * 멤버별 주간 인증 횟수를 조회합니다.
 */
export const getMemberVerificationCounts = (challengeId: number) => {
  return apiFetch<MemberVerificationCount[]>(`/api/challenge-verifications/${challengeId}/member/verification-count`, {
    method: 'GET',
    schema: z.array(MemberVerificationCountSchema),
  });
};

/**
 * 챌린지 인증을 생성합니다.
 * @param challengeId 챌린지 ID
 * @param formData 인증 요청 데이터 (image 파트 포함)
 */
export const createVerification = (challengeId: number, formData: FormData) => {
  return apiFetch<ChallengeVerificationResponse>(`/api/challenge-verifications/${challengeId}`, {
    method: 'POST',
    body: formData,
    schema: ChallengeVerificationResponseSchema,
  });
};

/**
 * 챌린지 인증을 삭제합니다.
 * @param verificationId 인증 ID
 */
export const deleteVerification = (verificationId: number) => {
    return apiFetch<void>(`/api/challenge-verifications/${verificationId}`, {
        method: 'DELETE',
    });
};
