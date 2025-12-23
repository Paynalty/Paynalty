export type ChallengeStatus = 'completed' | 'in_progress' | 'pending';

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
  id: string;
  title: string;
  status: ChallengeStatus;
  currentCount: number;
  totalCount: number;
  penaltyAmount: number;
  participants: string;
  participantCount: number;
  remainingTime?: string;
  deadline: string;
  verificationTime: string;
  verificationFrequency: string;
  verificationMethod: string;
  guideline?: string;
  recentVerification?: UserVerification;
  weeklyStatus?: WeeklyStatus[];
};
