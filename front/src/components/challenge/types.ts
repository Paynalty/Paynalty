export type ChallengeStatus = 'COMPLETE' | 'ACTIVE' | 'PENDING';

export type UserVerification = {
  name: string;
  avatar: string;
  image: string;
  date: string;
  time: string;
};

export type WeeklyStatus = {
  xAxisLabel: string;
  value: number;
};

export type Challenge = {
  challengeId: string;
  id?: string;
  title: string;
  status: ChallengeStatus;
  currentCount: number;
  penaltyAmount: number;
  participants: string;
  participantCount: number;
  verifyStartAt?: string;
  verifyEndAt: string;
  verificationFrequency: string;
  verificationType: string;
  guideline?: string;
  recentVerification?: UserVerification;
  weeklyStatus?: WeeklyStatus[];
};
