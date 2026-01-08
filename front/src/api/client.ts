import { ENV } from '../config/env';
import { Storage } from '@apps-in-toss/framework';
import { logout, getIsLoggedIn } from '../stores/authStore';
import { ApiClient, ApiOptions, ApiError } from './core/ApiClient';

// ApiError를 여기서 다시 export 하여 기존 코드 호환성 유지
export { ApiError, type ApiOptions };

export const api = new ApiClient({
  baseUrl: ENV.API_BASE_URL,
  getToken: async () => await Storage.getItem('accessToken'),
  onUnauthorized: async () => {
    // 중복 실행 방지
    if (getIsLoggedIn()) {
      console.log('🔒 [ApiClient] Triggering logout due to 401');
      await logout();
      
      // 웹 환경이거나 location 객체가 있다면 강제 리다이렉트 시도
      if (typeof window !== 'undefined' && window.location) {
        window.location.href = '/auth/login';
      }
    }
  },
});

/**
 * 기존 코드와의 호환성을 위한 래퍼 함수입니다.
 * 이제 내부 구현은 ApiClient 클래스에 위임됩니다.
 */
export async function apiFetch<T>(path: string, options: ApiOptions = {}): Promise<T> {
  return api.fetch<T>(path, options);
}
