// 네비게이션 타입 정의
export type RootStackParamList = {
  // 토스 앱빌더에서 만든 화면들의 파라미터 타입을 정의하세요

  Home: undefined;
  Login: undefined;
  Signup: undefined;

  // 페널티 관련
  PenaltyList: undefined;
  PenaltyDetail: {
    penaltyId: number;
  };

  // 결제 관련
  Payment: {
    penaltyId: number;
    amount: number;
  };
  PaymentResult: {
    success: boolean;
    paymentId?: number;
    message?: string;
  };

  // 사용자 관련
  Profile: undefined;
  Settings: undefined;
};

// 네비게이션 Props 타입 헬퍼
import { StackNavigationProp } from '@react-navigation/stack';
import { RouteProp } from '@react-navigation/native';

export type RootStackNavigationProp<T extends keyof RootStackParamList> =
  StackNavigationProp<RootStackParamList, T>;

export type RootStackRouteProp<T extends keyof RootStackParamList> =
  RouteProp<RootStackParamList, T>;