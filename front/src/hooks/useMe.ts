import { useQuery } from '@tanstack/react-query';
import { getMe } from '../api/users';
import { useAuthStore } from '../stores/authStore';

export const useMe = () => {
    const { isLoggedIn } = useAuthStore();
    return useQuery({
        queryKey: ['me'],
        queryFn: getMe,
        enabled: isLoggedIn,
        staleTime: Infinity, // 프로필은 잘 안 변한다고 가정
    });
};
