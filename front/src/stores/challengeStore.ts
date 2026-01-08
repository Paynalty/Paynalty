
import { create } from 'zustand';
import { ChallengeResponse } from '../api/challenges';

interface ChallengeStore {
  selectedChallengeId: number | null;
  selectedChallengeObject: ChallengeResponse | null;
  setSelectedChallengeId: (id: number | null) => void;
  setSelectedChallenge: (challenge: ChallengeResponse | null) => void;
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
export const setSelectedChallengeId = (id: number | null) => useChallengeStore.getState().setSelectedChallengeId(id);
export const setSelectedChallenge = (challenge: ChallengeResponse | null) =>
  useChallengeStore.getState().setSelectedChallenge(challenge);
export const getSelectedChallengeId = () => useChallengeStore.getState().selectedChallengeId;
export const getSelectedChallenge = () => useChallengeStore.getState().selectedChallengeObject;
