import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: (failureCount, error) => {
        // 401 에러(인증 실패)인 경우 재시도 하지 않음 (무한 루프 방지)
        if (error instanceof ApiError && error.status === 401) {
          return false;
        }
        // 그 외 에러는 최대 1번 재시도
        return failureCount < 1;
      },
      staleTime: 1000 * 60 * 5, // 5분간 데이터가 신선하다고 간주
      gcTime: 1000 * 60 * 10, // 10분간 캐시 유지
    },
  },
});
