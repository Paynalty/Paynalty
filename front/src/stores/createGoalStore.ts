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

// 전역 상태 (목표 생성 중에만 사용)
let createGoalData: CreateGoalData = {};

// 데이터 가져오기
export const getCreateChellengeData = (): CreateGoalData => {
  return createGoalData;
};

// 데이터 업데이트
export const updateCreateGoalData = (data: Partial<CreateGoalData>) => {
  createGoalData = { ...createGoalData, ...data };
};

// 데이터 초기화 (목표 생성 완료 후)
export const resetCreateGoalData = () => {
  createGoalData = {};
};
