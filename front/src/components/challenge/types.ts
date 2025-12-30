export type ChallengeStatus = 'COMPLETE' | 'ACTIVE' | 'PENDING';

export type Challenge = {
  id: string;
  title: string;
  status: ChallengeStatus;
  weeklyProgressCount: number;
  weeklyRequiredCount: string;
  penaltyAmount: number;
  participants: string;
  participantCount: number;
  verifyStart?: string;
  verifyEnd: string;
  daysOfWeek: string[];
  endAt: string;
  verificationType: string;
};
