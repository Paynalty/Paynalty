import { z } from 'zod';
import { apiFetch } from './client';

export const VerificationTypeSchema = z.enum(['PHOTO', 'TEXT', 'VOTE']);
export type VerificationType = z.infer<typeof VerificationTypeSchema>;

export const DayOfWeekSchema = z.enum(['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']);
export type DayOfWeekType = z.infer<typeof DayOfWeekSchema>;

export const CreateChallengeRequestSchema = z.object({
  title: z.string(),
  startDate: z.string(),
  endDate: z.string(),
  frequency: z.number().optional(),
  dayOfWeek: z.array(DayOfWeekSchema).optional(),
  penaltyAmount: z.number(),
  verifyStartAt: z.string().optional(),
  verifyEndAt: z.string().optional(),
  verificationType: VerificationTypeSchema,
  userIds: z.array(z.number()).optional(),
});
export type CreateChallengeRequest = z.infer<typeof CreateChallengeRequestSchema>;

export const CreateChallengeResponseSchema = z.object({
  id: z.number(),
  title: z.string(),
  status: z.string(),
});
export type CreateChallengeResponse = z.infer<typeof CreateChallengeResponseSchema>;

export const ChallengeDetailResponseSchema = z.object({
  verificationStatus: z.string(),
  id: z.number(),
  title: z.string(),
  weeklyProgressCount: z.number(),
  weeklyRequiredCount: z.number(),
  penaltyAmount: z.number(),
  verifyStart: z.string(),
  verifyEnd: z.string(),
  verificationType: z.string(),
  daysOfWeek: z.array(z.string()),
  endAt: z.string(),
});
export type ChallengeDetailResponse = z.infer<typeof ChallengeDetailResponseSchema>;

export const createChallenge = (data: CreateChallengeRequest) => {
  return apiFetch<CreateChallengeResponse>('/api/challenge', {
    method: 'POST',
    body: JSON.stringify(data),
    schema: CreateChallengeResponseSchema,
  });
};

export const getMyProgressChallenges = (userId: number = 1, status: 'PENDING' | 'ACTIVE' | 'COMPLETE' = 'ACTIVE') => {
  return apiFetch<ChallengeDetailResponse[]>(`/api/challenge/${userId}/${status}`, {
    method: 'GET',
    schema: z.array(ChallengeDetailResponseSchema),
  });
};
