import React, { createContext, useContext, useState, ReactNode } from 'react';

// 목표 생성 데이터 타입
export interface CreateGoalData {
    goalTitle?: string;           // Step 2: 목표 제목
    verificationMethod?: string;  // Step 6: 인증 방식
    penaltyAmount?: number | string; // Step 7: 패널티 금액
    customAmount?: string;        // Step 7: 직접 입력 금액
}

interface CreateGoalContextType {
    goalData: CreateGoalData;
    updateGoalData: (data: Partial<CreateGoalData>) => void;
    resetGoalData: () => void;
}

const CreateGoalContext = createContext<CreateGoalContextType | undefined>(undefined);

export function CreateGoalProvider({ children }: { children: ReactNode }) {
    const [goalData, setGoalData] = useState<CreateGoalData>({});

    const updateGoalData = (data: Partial<CreateGoalData>) => {
        setGoalData((prev) => ({ ...prev, ...data }));
    };

    const resetGoalData = () => {
        setGoalData({});
    };

    return (
        <CreateGoalContext.Provider value={{ goalData, updateGoalData, resetGoalData }}>
            {children}
        </CreateGoalContext.Provider>
    );
}

export function useCreateGoal() {
    const context = useContext(CreateGoalContext);
    if (!context) {
        throw new Error('useCreateGoal must be used within CreateGoalProvider');
    }
    return context;
}