import { create } from 'zustand';
import { Challenge } from '../components/challenge/types';

// Mock 데이터
export const MOCK_CHALLENGES: Challenge[] = [
  // ... (기존 데이터와 동일)
  {
    id: '1',
    title: '운동 30분 챌린지',
    status: 'completed',
    currentCount: 3,
    totalCount: 3,
    penaltyAmount: 5000,
    participants: '민수,지은,영지··',
    participantCount: 5,
    deadline: '2025년 8월 10일',
    verificationTime: '06:00 ~ 09:00',
    verificationFrequency: '매일',
    verificationMethod: '사진',
    guideline: '오늘 운동한 모습을 사진으로 찍어 남겨주세요.',
    weeklyStatus: [
      { xAxisLabel: '나', value: 3 },
      { xAxisLabel: '민수', value: 3 },
      { xAxisLabel: '지은', value: 2 },
    ],
  },
  {
    id: '2',
    title: '매일 만보 걷기',
    status: 'in_progress',
    currentCount: 3,
    totalCount: 7,
    penaltyAmount: 5000,
    participants: '하트브레이커',
    participantCount: 6,
    remainingTime: '5시간 31분',
    deadline: '2025년 1월 15일',
    verificationTime: '14:00 ~ 23:00',
    verificationFrequency: '월, 화, 목 / 3회',
    verificationMethod: '사진',
    guideline: '오늘 걸음 수가 명확히 보이는 화면을 캡처해서 올려주세요.',
    recentVerification: {
      name: '지은',
      avatar: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png',
      image: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png',
      date: '2025년 8월 15일 월요일',
      time: '오전 10:58',
    },
    weeklyStatus: [
      { xAxisLabel: '나', value: 2 },
      { xAxisLabel: '길동', value: 3 },
      { xAxisLabel: '형욱', value: 3 },
      { xAxisLabel: '지은', value: 2 },
      { xAxisLabel: '은채', value: 1 },
    ],
  },
  {
    id: '3',
    title: '영어 단어 20개 암기',
    status: 'in_progress',
    currentCount: 1,
    totalCount: 5,
    penaltyAmount: 3000,
    participants: '스터디A',
    participantCount: 4,
    remainingTime: '2시간 10분',
    deadline: '2025년 9월 20일',
    verificationTime: '20:00 ~ 23:59',
    verificationFrequency: '주 5회',
    verificationMethod: '텍스트',
    guideline: '오늘 암기한 영어 단어 20개를 텍스트로 입력해주세요.',
  },
  {
    id: '4',
    title: '야식 금지 챌린지',
    status: 'pending',
    currentCount: 2,
    totalCount: 3,
    penaltyAmount: 10000,
    participants: '한우,한돈',
    participantCount: 2,
    deadline: '2025년 10월 1일',
    verificationTime: '22:00 ~ 02:00',
    verificationFrequency: '매일',
    verificationMethod: '사진',
  },
  {
    id: '5',
    title: '독서 30분',
    status: 'completed',
    currentCount: 7,
    totalCount: 7,
    penaltyAmount: 2000,
    participants: '북클럽',
    participantCount: 8,
    deadline: '2025년 7월 30일',
    verificationTime: '자율',
    verificationFrequency: '주 3회',
    verificationMethod: '텍스트',
  },
];

interface ChallengeStore {
  selectedChallengeId: string | null;
  selectedChallengeObject: Challenge | null;
  setSelectedChallengeId: (id: string | null) => void;
  setSelectedChallenge: (challenge: Challenge | null) => void;
}

export const useChallengeStore = create<ChallengeStore>((set) => ({
  selectedChallengeId: null,
  selectedChallengeObject: null,
  setSelectedChallengeId: (id) => set({ selectedChallengeId: id, selectedChallengeObject: null }),
  setSelectedChallenge: (challenge) =>
    set({
      selectedChallengeId: challenge?.id ?? null,
      selectedChallengeObject: challenge,
    }),
}));

// 하위 호환성을 위한 헬퍼 함수
export const getChallenges = (): Challenge[] => MOCK_CHALLENGES;
export const getChallengeById = (id: string): Challenge | undefined => MOCK_CHALLENGES.find((c) => c.id === id);
export const setSelectedChallengeId = (id: string | null) => useChallengeStore.getState().setSelectedChallengeId(id);
export const setSelectedChallenge = (challenge: Challenge | null) =>
  useChallengeStore.getState().setSelectedChallenge(challenge);
export const getSelectedChallengeId = () => useChallengeStore.getState().selectedChallengeId;
export const getSelectedChallenge = () => {
  const { selectedChallengeObject, selectedChallengeId } = useChallengeStore.getState();
  if (selectedChallengeObject) return selectedChallengeObject;
  if (!selectedChallengeId) return undefined;
  return getChallengeById(selectedChallengeId);
};
