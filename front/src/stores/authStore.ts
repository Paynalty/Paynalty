import {create} from 'zustand';
import {Storage} from '@apps-in-toss/framework';

interface AuthState {
  isLoggedIn: boolean;
  setLoggedIn: (loggedIn: boolean) => void;
  logout: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
  isLoggedIn: false,
  setLoggedIn: (loggedIn: boolean) => set({ isLoggedIn: loggedIn }),
  logout: async () => {
    try {
      await Storage.removeItem('accessToken');
      set({ isLoggedIn: false });
      // console.log('🔓 [AuthStore] Logged out and token removed');
    } catch (error) {
      console.error('Failed to remove access token during logout:', error);
    }
  },
}));

// Helper functions for non-hook environments (like apiFetch)
export const logout = () => useAuthStore.getState().logout();
export const setLoggedIn = (loggedIn: boolean) => useAuthStore.getState().setLoggedIn(loggedIn);
export const getIsLoggedIn = () => useAuthStore.getState().isLoggedIn;
