import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import { RootStackParamList } from './types';

// 화면 import (토스 앱빌더에서 만든 화면들을 여기에 import)
// import HomeScreen from '@screens/HomeScreen';
// import LoginScreen from '@screens/LoginScreen';
// 등등...

const Stack = createStackNavigator<RootStackParamList>();

const RootNavigator = () => {
  return (
    <Stack.Navigator
      initialRouteName="Home"
      screenOptions={{
        headerShown: false,
      }}
    >
      {/*
        토스 앱빌더에서 만든 화면들을 여기에 추가하세요

        예시:
        <Stack.Screen name="Home" component={HomeScreen} />
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="PenaltyList" component={PenaltyListScreen} />
        <Stack.Screen name="PenaltyDetail" component={PenaltyDetailScreen} />
        <Stack.Screen name="Payment" component={PaymentScreen} />
      */}
      <Stack.Screen
        name="Home"
        component={() => null}
        options={{ headerShown: true, title: '홈' }}
      />
    </Stack.Navigator>
  );
};

export default RootNavigator;