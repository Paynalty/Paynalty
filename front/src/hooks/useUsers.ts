import { useQuery } from '@tanstack/react-query';
import { searchUsers } from '../api/users';

/**
 * 사용자 검색 Hook
 * @param keyword 검색어
 * @param enabled 쿼리 활성화 여부
 */
export const useUserSearch = (keyword: string, enabled: boolean = true) => {
  return useQuery({
    queryKey: ['userSearch', keyword],
    queryFn: async () => {
      if (!keyword || keyword.trim().length < 2) {
        return [];
      }
      return await searchUsers(keyword.trim());
    },
    enabled: enabled && keyword.trim().length >= 2, // 2글자 이상만 검색
    staleTime: 30000, // 30초 캐싱
  });
};
