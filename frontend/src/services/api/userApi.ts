import apiClient from './client';
import { ApiResponse } from '@types/api';
import { User, LoginRequest, LoginResponse, SignupRequest } from '@types/user';

// 사용자 관련 API
export const userApi = {
  // 로그인
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<ApiResponse<LoginResponse>>('/auth/login', data);
    return response.data.data!;
  },

  // 회원가입
  signup: async (data: SignupRequest): Promise<User> => {
    const response = await apiClient.post<ApiResponse<User>>('/auth/signup', data);
    return response.data.data!;
  },

  // 로그아웃
  logout: async (): Promise<void> => {
    await apiClient.post<ApiResponse<void>>('/auth/logout');
  },

  // 내 정보 조회
  getMe: async (): Promise<User> => {
    const response = await apiClient.get<ApiResponse<User>>('/users/me');
    return response.data.data!;
  },

  // 내 정보 수정
  updateMe: async (data: Partial<User>): Promise<User> => {
    const response = await apiClient.patch<ApiResponse<User>>('/users/me', data);
    return response.data.data!;
  },

  // 사용자 조회 (ID)
  getUserById: async (id: number): Promise<User> => {
    const response = await apiClient.get<ApiResponse<User>>(`/users/${id}`);
    return response.data.data!;
  },
};