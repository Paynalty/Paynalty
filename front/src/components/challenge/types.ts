export type ChallengeStatus = 'completed' | 'in_progress' | 'pending';

export type Challenge = {
    id: string;
    title: string;
    status: ChallengeStatus;
    currentCount: number;
    totalCount: number;
    penaltyAmount: number;
    participants: string;
    remainingTime?: string;
};
