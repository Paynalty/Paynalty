// 페널티 관련 타입 정의
export interface Penalty {
  id: number;
  userId: number;
  amount: number;
  reason: string;
  status: PenaltyStatus;
  dueDate: string;
  paidAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export enum PenaltyStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
}

export interface CreatePenaltyRequest {
  userId: number;
  amount: number;
  reason: string;
  dueDate: string;
}

export interface PenaltyListItem {
  id: number;
  amount: number;
  reason: string;
  status: PenaltyStatus;
  dueDate: string;
  createdAt: string;
}