import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1, // API 실패 시 1회 재시도 (필요에 따라 조정)
      staleTime: 1000 * 60 * 5, // 5분간 데이터가 신선하다고 간주 (API 재호출 안 함)
      gcTime: 1000 * 60 * 10, // 10분간 캐시 유지 (v5부터 cacheTime -> gcTime)
    },
  },
});
