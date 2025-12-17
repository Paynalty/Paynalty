// 결제 관련 타입 정의
export interface Payment {
  id: number;
  userId: number;
  penaltyId: number;
  amount: number;
  status: PaymentStatus;
  method: PaymentMethod;
  transactionId: string | null;
  createdAt: string;
  updatedAt: string;
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
}

export enum PaymentMethod {
  CARD = 'CARD',
  BANK_TRANSFER = 'BANK_TRANSFER',
  TOSS_PAY = 'TOSS_PAY',
  KAKAO_PAY = 'KAKAO_PAY',
}

export interface PaymentRequest {
  penaltyId: number;
  amount: number;
  method: PaymentMethod;
}

export interface PaymentResponse {
  payment: Payment;
  penalty: {
    id: number;
    status: string;
  };
}