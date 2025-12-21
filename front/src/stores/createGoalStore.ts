// 목표 생성 데이터 타입
export interface CreateGoalData {
    goalTitle?: string;
    verificationMethod?: string;
    period?: string;
    selectionType?: 'day' | 'count';
    penaltyAmount?: number | string;
    customAmount?: string;
    deadline?: string;
    startTime?: string;
    endTime?: string;
}

// 전역 상태 (목표 생성 중에만 사용)
let createGoalData: CreateGoalData = {};

// 데이터 가져오기
export const getCreateGoalData = (): CreateGoalData => {
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