import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ErrorResponse } from '@types/api';

// API Base URL (개발/운영 환경에 따라 변경)
const API_BASE_URL = __DEV__
  ? 'http://localhost:8080/api'
  : 'https://api.paynalty.com/api';

// Axios 인스턴스 생성
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request 인터셉터: JWT 토큰 자동 추가
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // 토큰 가져오기 (AsyncStorage 또는 Zustand에서)
    // const token = await getAccessToken();
    // if (token && config.headers) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response 인터셉터: 에러 처리
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error: AxiosError<ErrorResponse>) => {
    if (error.response) {
      const { status, data } = error.response;

      // 401 Unauthorized: 토큰 갱신 또는 로그아웃
      if (status === 401) {
        // TODO: 리프레시 토큰으로 액세스 토큰 갱신
        // TODO: 실패 시 로그아웃 처리
        console.error('Unauthorized - 로그인이 필요합니다');
      }

      // 403 Forbidden: 권한 없음
      if (status === 403) {
        console.error('Forbidden - 권한이 없습니다');
      }

      // 500 Internal Server Error
      if (status >= 500) {
        console.error('Server Error - 서버 오류가 발생했습니다');
      }

      // ErrorResponse 형식으로 에러 전달
      return Promise.reject({
        status: data?.status || status,
        code: data?.code || 'UNKNOWN_ERROR',
        message: data?.message || error.message,
        timestamp: data?.timestamp || new Date().toISOString(),
      } as ErrorResponse);
    }

    // 네트워크 에러
    if (error.request) {
      return Promise.reject({
        status: 0,
        code: 'NETWORK_ERROR',
        message: '네트워크 연결을 확인해주세요',
        timestamp: new Date().toISOString(),
      } as ErrorResponse);
    }

    return Promise.reject(error);
  }
);

export default apiClient;