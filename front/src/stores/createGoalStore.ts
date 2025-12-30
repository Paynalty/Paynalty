import { create } from 'zustand';

// 목표 생성 데이터 타입
export interface CreateGoalData {
  title?: string;
  verificationType?: string;
  period?: string;
  startDate?: 'day' | 'count';
  penaltyAmount?: number | string;
  customAmount?: string;
  endDate?: string;
  verifyStartAt?: string;
  verifyEndAt?: string;
  dayOfWeeks?: string[]; // 영문 요일 (MON, TUE, ...)
  frequency?: number; // 주 n회
}

interface CreateGoalStore {
  data: CreateGoalData;
  updateData: (data: Partial<CreateGoalData>) => void;
  resetData: () => void;
}

export const useCreateGoalStore = create<CreateGoalStore>((set) => ({
  data: {},
  updateData: (newData) =>
    set((state) => ({
      data: { ...state.data, ...newData },
    })),
  resetData: () => set({ data: {} }),
}));