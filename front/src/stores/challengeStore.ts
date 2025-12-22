import { Challenge } from '../components/challenge/types';

// Mock 데이터
const MOCK_CHALLENGES: Challenge[] = [
    {
        id: '1',
        title: '운동 30분 챌린지',
        status: 'completed',
        currentCount: 3,
        totalCount: 3,
        penaltyAmount: 5000,
        participants: '민수,지은,영지··',
    },
    {
        id: '2',
        title: '매일 만보 걷기',
        status: 'in_progress',
        currentCount: 3,
        totalCount: 7,
        penaltyAmount: 5000,
        participants: '하트브레이커',
        remainingTime: '5시간 31분',
    },
    {
        id: '3',
        title: '영어 단어 20개 암기',
        status: 'in_progress',
        currentCount: 1,
        totalCount: 5,
        penaltyAmount: 3000,
        participants: '스터디A',
        remainingTime: '2시간 10분',
    },
    {
        id: '4',
        title: '야식 금지 챌린지',
        status: 'pending',
        currentCount: 2,
        totalCount: 3,
        penaltyAmount: 10000,
        participants: '한우,한돈',
    },
    {
        id: '5',
        title: '독서 30분',
        status: 'completed',
        currentCount: 7,
        totalCount: 7,
        penaltyAmount: 2000,
        participants: '북클럽',
    },
];

// 선택된 challenge ID 저장
let selectedChallengeId: string | null = null;

// 모든 challenges 조회
export const getChallenges = (): Challenge[] => {
    return MOCK_CHALLENGES;
};

// ID로 특정 challenge 조회
export const getChallengeById = (id: string): Challenge | undefined => {
    return MOCK_CHALLENGES.find(challenge => challenge.id === id);
};

// 선택된 challenge ID 설정
export const setSelectedChallengeId = (id: string) => {
    selectedChallengeId = id;
};

// 선택된 challenge ID 조회
export const getSelectedChallengeId = (): string | null => {
    return selectedChallengeId;
};

// 선택된 challenge 전체 데이터 조회
export const getSelectedChallenge = (): Challenge | undefined => {
    if (!selectedChallengeId) return undefined;
    return getChallengeById(selectedChallengeId);
};
