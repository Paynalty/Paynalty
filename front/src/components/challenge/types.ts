export type verificationStatus = 'VERIFIED' | 'NOT_VERIFIED';

export type Challenge = {
  id: string;
  title: string;
  verificationStatus: verificationStatus;
  weeklyProgressCount: number;
  weeklyRequiredCount: string;
  penaltyAmount: number;
  participants: string;
  participantCount: number;
  verifyStart: string;
  verifyEnd: string;
  daysOfWeek: string[];
  endAt: string;
  verificationType: string;
  status?: 'ACTIVE' | 'PENDING' | 'COMPLETE';
};
