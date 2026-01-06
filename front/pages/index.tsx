import { createRoute, Spacing } from '@granite-js/react-native';
import { View, StyleSheet, ScrollView } from 'react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import React, { useState, useEffect, useMemo } from 'react';
import { Storage } from '@apps-in-toss/framework';
import { ChallengeCard } from '../src/components/challenge/ChallengeCard';
import { useChallenges, useMissionChallenges } from '../src/hooks/useChallenges';
import { isTodayChallenge } from '../src/utils/challenge';
import { OnboardingView } from '../src/components/home/OnboardingView';
import { MissionSection } from '../src/components/home/MissionSection';
import { StatusFilterSection } from '../src/components/home/StatusFilterSection';

import { useAuthStore, setLoggedIn } from '../src/stores/authStore';

export const Route = createRoute('/', {
  component: Page,
});

function Page() {
  const { isLoggedIn } = useAuthStore();
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();

  useEffect(() => {
    checkAuthAndOnboarding();
  }, [isLoggedIn]); // isLoggedIn 상태가 변할 때마다 체크

  const checkAuthAndOnboarding = async () => {
    try {
        // 로그인 테스트용
        // await Storage.removeItem('accessToken')
        // await Storage.removeItem('hasCompletedOnboarding')
      const token = await Storage.getItem('accessToken');
      if (token) {
        setLoggedIn(true);
        return;
      }

      setLoggedIn(false);
      const hasCompletedOnboarding = await Storage.getItem('hasCompletedOnboarding');
      if (!hasCompletedOnboarding) {
        navigation.navigate('/auth');
      } else {
        navigation.navigate('/auth/login');
      }
    } catch (error) {
      console.error('Failed to check auth status:', error);
    }
  };

  const [currentStatus, setCurrentStatus] = useState<'ACTIVE' | 'PENDING' | 'COMPLETE'>('ACTIVE');

  const { data: challenges = [] } = useChallenges(currentStatus);
  const { data: missionChallenges = [] } = useMissionChallenges();

  const todayMissions = useMemo(
    () =>
      missionChallenges.filter((mission) =>
        isTodayChallenge(mission.daysOfWeek, Number(mission.weeklyRequiredCount), mission.weeklyProgressCount)
      ),
    [missionChallenges]
  );

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <MissionSection missions={todayMissions} onCreateChallenge={() => navigation.navigate('/create-challenge')} />

      <Spacing size={12} />

      <StatusFilterSection currentStatus={currentStatus} onStatusChange={setCurrentStatus} />

      {/* 챌린지 카드 리스트 */}
      {challenges.length > 0 ? (
        <View style={{ paddingBottom: 40, paddingHorizontal: 0 }}>
          {challenges.map((challenge, index) => (
            <View key={challenge.id || `challenge-${index}`}>
              {index > 0 && <Spacing size={12} />}
              <ChallengeCard challenge={{ ...challenge, status: currentStatus }} />
            </View>
          ))}
        </View>
      ) : (
        <View style={{ flex: 1 }}>
          <OnboardingView
            adaptive={adaptive}
            onCreateObjective={() => navigation.navigate('/create-challenge')}
            currentStatus={currentStatus}
          />
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  content: {
    paddingBottom: 20,
  },
});
