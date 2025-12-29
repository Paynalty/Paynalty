import { create } from 'zustand';
import { Challenge } from '../components/challenge/types';

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
      selectedChallengeId: challenge?.challengeId ?? null,
      selectedChallengeObject: challenge,
    }),
}));

// 하위 호환성을 위한 헬퍼 함수
export const setSelectedChallengeId = (id: string | null) => useChallengeStore.getState().setSelectedChallengeId(id);
export const setSelectedChallenge = (challenge: Challenge | null) =>
  useChallengeStore.getState().setSelectedChallenge(challenge);
export const getSelectedChallengeId = () => useChallengeStore.getState().selectedChallengeId;
export const getSelectedChallenge = () => useChallengeStore.getState().selectedChallengeObject;
