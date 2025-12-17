import apiClient from './client';
import { ApiResponse, PageRequest, PageResponse } from '@types/api';
import { Payment, PaymentRequest, PaymentResponse } from '@types/payment';

// 결제 관련 API
export const paymentApi = {
  // 결제 처리
  processPayment: async (data: PaymentRequest): Promise<PaymentResponse> => {
    const response = await apiClient.post<ApiResponse<PaymentResponse>>('/payments', data);
    return response.data.data!;
  },

  // 결제 내역 조회 (내 결제)
  getMyPayments: async (params?: PageRequest): Promise<PageResponse<Payment>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<Payment>>>(
      '/payments/me',
      { params }
    );
    return response.data.data!;
  },

  // 결제 상세 조회
  getPaymentById: async (id: number): Promise<Payment> => {
    const response = await apiClient.get<ApiResponse<Payment>>(`/payments/${id}`);
    return response.data.data!;
  },

  // 결제 취소
  cancelPayment: async (id: number): Promise<Payment> => {
    const response = await apiClient.patch<ApiResponse<Payment>>(`/payments/${id}/cancel`);
    return response.data.data!;
  },
};