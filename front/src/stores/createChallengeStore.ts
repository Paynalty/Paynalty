import { create } from 'zustand';
import { UserResponse } from '../api/users';

// 챌린지 생성 데이터 타입
export interface CreateChallengeData {
  title?: string;
  verificationType?: string;
  period?: string;
  startDate?: 'day' | 'count';
  penaltyAmount?: number | string;
  customAmount?: string;
  endDate?: string;
  verifyStartAt?: string;
  verifyEndAt?: string;
  daysOfWeek?: string[];
  frequency?: number;
  invitedUsers?: UserResponse[];
  isEditing?: boolean;
  challengeId?: string;
}

interface CreateChallengeStore {
  data: CreateChallengeData;
  updateData: (data: Partial<CreateChallengeData>) => void;
  resetData: () => void;
}

export const useCreateChallengeStore = create<CreateChallengeStore>((set) => ({
  data: {},
  updateData: (newData) =>
    set((state) => ({
      data: { ...state.data, ...newData },
    })),
  resetData: () => set({ data: {} }),
}));
