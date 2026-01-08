import { z } from 'zod';
import { apiFetch } from './client';

export const UserResponseSchema = z.object({
  id: z.number(),
  tossId: z.number(),
  name: z.string(),
  email: z.string(),
});
export type UserResponse = z.infer<typeof UserResponseSchema>;

/**
 * 현재 로그인한 사용자의 정보를 조회합니다.
 */
export const getMe = () => {
    return apiFetch<UserResponse>('/api/users/me', {
        method: 'GET',
        schema: UserResponseSchema,
    });
};

/**
 * 키워드로 사용자를 검색합니다.
 * @param keyword 검색어 (이름 또는 이메일)
 */
export const searchUsers = (keyword: string) => {
  return apiFetch<UserResponse[]>(`/api/users/all?keyword=${encodeURIComponent(keyword)}`, {
    method: 'GET',
    schema: z.array(UserResponseSchema),
  });
};
