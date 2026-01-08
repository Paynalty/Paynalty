import {z} from 'zod';
import {apiFetch} from './client';

export const VerificationTypeSchema = z.string().default('PHOTO');
export type VerificationType = z.infer<typeof VerificationTypeSchema>;

export const DayOfWeekSchema = z.enum(['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']);
export type DayOfWeekType = z.infer<typeof DayOfWeekSchema>;

export const CreateChallengeRequestSchema = z.object({
  title: z.string(),
  startDate: z.string().optional().nullable(),
  endDate: z.string(),
  frequency: z.number().optional().nullable(),
  daysOfWeek: z.array(z.string()).optional().nullable(),
  penaltyAmount: z.number(),
  verifyStartAt: z.string().optional().nullable(),
  verifyEndAt: z.string().optional().nullable(),
  verificationType: VerificationTypeSchema,
  tossIds: z.array(z.number()).optional().nullable(),
});
export type CreateChallengeRequest = z.infer<typeof CreateChallengeRequestSchema>;

export const CreateChallengeResponseSchema = z.number();
export type CreateChallengeResponse = z.infer<typeof CreateChallengeResponseSchema>;

export const VerificationStatusSchema = z.enum(['VERIFIED', 'NOT_VERIFIED']);
export type VerificationStatus = z.infer<typeof VerificationStatusSchema>;

export const ChallengeResponseSchema = z.object({
  status: z.enum(['ACTIVE', 'PENDING', 'COMPLETE']),
  verificationStatus: VerificationStatusSchema,
  id: z.coerce.number(),
  title: z.coerce.string(),
  weeklyProgressCount: z.coerce.number(),
  weeklyRequiredCount: z.coerce.number(),
  penaltyAmount: z.coerce.number(),
  verifyStart: z.coerce.string(),
  verifyEnd: z.coerce.string(),
  verificationType: z.coerce.string(),
  daysOfWeek: z.array(z.string()).default([]),
  startAt: z.string().optional(),
  endAt: z.coerce.string(),
  members: z.array(z.object({
    id: z.coerce.number(),
    tossId: z.coerce.number(),
    userName: z.string(),
    challengeId: z.coerce.number(),
    joinedAt: z.string().optional(),
    isSuccess: z.string().optional(),
    endAt: z.string().optional(),
    role: z.enum(['CREATOR', 'CHALLENGER']).optional(),
  })).optional().default([]),
});
export type ChallengeResponse = z.infer<typeof ChallengeResponseSchema>;

export const createChallenge = (data: CreateChallengeRequest) => {
  return apiFetch<CreateChallengeResponse>('/api/challenge', {
    method: 'POST',
    body: JSON.stringify(data),
    schema: CreateChallengeResponseSchema,
  });
};

export const getChallengeDetail = (challengeId: number) => {
  return apiFetch<ChallengeResponse>(`/api/challenge/${challengeId}/detail`, {
    method: 'GET',
    schema: ChallengeResponseSchema,
  });
};

export const getMyProgressChallenges = (status: 'PENDING' | 'ACTIVE' | 'COMPLETE' = 'ACTIVE') => {
  return apiFetch<ChallengeResponse[]>(`/api/challenge/${status}`, {
    method: 'GET',
    schema: z.array(ChallengeResponseSchema),
  });
};

/**
 * 수정을 위한 챌린지 기존 정보를 조회합니다.
 */
export const getChallengeEditForm = (challengeId: string) => {
  return apiFetch<CreateChallengeRequest>(`/api/challenge/${challengeId}/edit`, {
    method: 'GET',
    schema: CreateChallengeRequestSchema,
  });
};

/**
 * 챌린지 정보를 수정합니다.
 */
export const updateChallenge = (challengeId: string, data: CreateChallengeRequest) => {
  return apiFetch<ChallengeResponse>(`/api/challenge/${challengeId}/`, {
    method: 'PUT',
    body: JSON.stringify(data),
    schema: ChallengeResponseSchema,
  });
};

/**
 * 챌린지를 삭제합니다.
 */
export const deleteChallenge = (challengeId: string) => {
  return apiFetch<void>(`/api/challenge/${challengeId}`, {
    method: 'DELETE',
    schema: z.void(),
  });
};
