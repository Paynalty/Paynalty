// API 응답 타입 정의 (백엔드 ApiResponse와 동일)
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  message: string | null;
}

// API 에러 응답 타입 (백엔드 ErrorResponse와 동일)
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  code: string;
  message: string;
}

// 공통 페이지네이션
export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}