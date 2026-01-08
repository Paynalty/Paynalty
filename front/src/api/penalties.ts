import {z} from 'zod';
import {apiFetch} from './client';

export const PenaltyResponseSchema = z.object({
  penaltyId: z.coerce.number(),
  challengeMemberId: z.coerce.number(),
  challengeTitle: z.string(),
  amount: z.coerce.number(),
  memberName: z.string(),
  paid: z.boolean(),
  paidAt: z.string().nullable().optional(), // LocalDateTime comes as string usually
  createdAt: z.string(),
});

export type PenaltyResponse = z.infer<typeof PenaltyResponseSchema>;

/**
 * 본인 벌금 내역 조회
 */
export const getMyPenalties = (challengeId: number) => {
  return apiFetch<PenaltyResponse[]>(`/api/penalties/${challengeId}`, {
    method: 'GET',
    schema: z.array(PenaltyResponseSchema),
  });
};

/**
 * 챌린지의 모든 벌금 내역 조회
 */
export const getAllPenalties = (challengeId: number) => {
  return apiFetch<PenaltyResponse[]>(`/api/penalties/${challengeId}/all`, {
    method: 'GET',
    schema: z.array(PenaltyResponseSchema),
  });
};

/**
 * 벌금 납부 상태 변경
 */
export const updatePenaltyStatus = (penaltyId: number, paid: boolean) => {
  return apiFetch(`/api/penalties/${penaltyId}`, {
    method: 'PUT',
    body: JSON.stringify({ paid }),
  });
};
