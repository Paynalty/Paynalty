import { apiFetch } from './client';

export interface UserResponse {
  userId: number;
  tossId: number;
  name: string;
  email: string;
}

/**
 * 키워드로 사용자를 검색합니다.
 * @param keyword 검색어 (이름 또는 이메일)
 */
export const searchUsers = (keyword: string) => {
  return apiFetch<UserResponse[]>(
    `/api/users/all?keyword=${encodeURIComponent(keyword)}`,
    { method: 'GET' }
  );
};
