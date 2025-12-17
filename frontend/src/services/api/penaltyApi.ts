import apiClient from './client';
import { ApiResponse, PageRequest, PageResponse } from '@types/api';
import { Penalty, PenaltyListItem, CreatePenaltyRequest } from '@types/penalty';

// 페널티 관련 API
export const penaltyApi = {
  // 페널티 목록 조회 (내 페널티)
  getMyPenalties: async (params?: PageRequest): Promise<PageResponse<PenaltyListItem>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<PenaltyListItem>>>(
      '/penalties/me',
      { params }
    );
    return response.data.data!;
  },

  // 페널티 상세 조회
  getPenaltyById: async (id: number): Promise<Penalty> => {
    const response = await apiClient.get<ApiResponse<Penalty>>(`/penalties/${id}`);
    return response.data.data!;
  },

  // 페널티 생성 (관리자)
  createPenalty: async (data: CreatePenaltyRequest): Promise<Penalty> => {
    const response = await apiClient.post<ApiResponse<Penalty>>('/penalties', data);
    return response.data.data!;
  },

  // 페널티 취소 (관리자)
  cancelPenalty: async (id: number): Promise<Penalty> => {
    const response = await apiClient.patch<ApiResponse<Penalty>>(`/penalties/${id}/cancel`);
    return response.data.data!;
  },

  // 미납 페널티 합계 조회
  getUnpaidTotal: async (): Promise<number> => {
    const response = await apiClient.get<ApiResponse<{ total: number }>>('/penalties/unpaid/total');
    return response.data.data!.total;
  },
};