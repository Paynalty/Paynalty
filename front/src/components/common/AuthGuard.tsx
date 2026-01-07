import React from 'react';
import { useAuthStore } from '../../stores/authStore';
import { View, StyleSheet } from 'react-native';
import { Txt, Button, Asset } from '@toss/tds-react-native';
import { useNavigation, Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';

interface AuthGuardProps {
  children: React.ReactNode;
}

export const AuthGuard = ({ children }: AuthGuardProps) => {
  const { isLoggedIn } = useAuthStore();
  const adaptive = useAdaptive();
  const navigation = useNavigation();

  if (!isLoggedIn) {
    return (
      <View style={styles.container}>
        <Asset.Image
          frameShape={{ width: 120, height: 120 }}
          source={{ uri: 'https://static.toss.im/ml-product/farmer-golden-rice-plant.png' }}
        />
        <Spacing size={24} />
        <Txt typography="t4" fontWeight="bold" color={adaptive.grey800}>
          로그인이 필요한 기능이에요
        </Txt>
        <Spacing size={8} />
        <Txt typography="t6" color={adaptive.grey600} textAlign="center">
          챌린지를 만들고 목표를 달성하려면{'\n'}
          먼저 로그인을 해주세요.
        </Txt>
        <Spacing size={32} />
        <View style={{ width: 180 }}>
          <Button
            type="primary"
            style="fill"
            onPress={() => navigation.navigate('/auth/login')}
          >
            로그인하러 가기
          </Button>
        </View>
      </View>
    );
  }

  return <>{children}</>;
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 24,
    backgroundColor: 'white',
  },
});
