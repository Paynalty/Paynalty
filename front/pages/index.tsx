import { createRoute, Spacing } from '@granite-js/react-native';
import { View, StyleSheet, ScrollView, Pressable } from 'react-native';
import { Asset, Top, ListRow, ListHeader, Icon, Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { useState, useEffect, useMemo } from 'react';
import { Storage } from '@apps-in-toss/framework';
import { ChallengeCard } from 'components/challenge/ChallengeCard';
import { useChallenges, useMissionChallenges } from '../src/hooks/useChallenges';
import { getVerificationMessage, isTodayChallenge } from '../src/utils/challenge';
import { OnboardingView } from '../src/components/home/OnboardingView';

export const Route = createRoute('/', {
  component: Page,
});

function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();

  useEffect(() => {
    checkOnboarding();
  }, []);

  const checkOnboarding = async () => {
    try {
        // 테스트용: 저장된 온보딩 상태 삭제
        await Storage.removeItem('hasCompletedOnboarding');
      const hasCompletedOnboarding = await Storage.getItem('hasCompletedOnboarding');
      if (!hasCompletedOnboarding) {
        navigation.navigate('/auth');
      }
    } catch (error) {
      console.error('Failed to check onboarding status:', error);
    }
  };

  const [showTooltip, setShowTooltip] = useState(true);
  const [isMissionExpanded, setIsMissionExpanded] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const [currentStatus, setCurrentStatus] = useState<'ACTIVE' | 'PENDING' | 'COMPLETE'>('ACTIVE');

  const { data: challenges = [] } = useChallenges(currentStatus);
  const { data: missionChallenges = [] } = useMissionChallenges();

  const todayMissions = missionChallenges.filter((mission) =>
    isTodayChallenge(mission.daysOfWeek, Number(mission.weeklyRequiredCount), mission.weeklyProgressCount)
  );

  const totalPenalty = useMemo(
    () => todayMissions.reduce((sum, challenge) => sum + challenge.penaltyAmount, 0),
    [todayMissions]
  );

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <Pressable onPress={() => setIsMissionExpanded(!isMissionExpanded)}>
        <View
          style={{
            backgroundColor: adaptive.blue500,
            borderBottomLeftRadius: 20,
            borderBottomRightRadius: 20,
            overflow: 'hidden',
          }}
        >
          <Top
            title={
              <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}>
                <Top.TitleParagraph color={adaptive.background}>오늘의 미션</Top.TitleParagraph>
                {todayMissions.length > 0 && (
                  <Txt typography="t5" fontWeight="bold" color={adaptive.background}>
                    {isMissionExpanded ? '∨' : '>'}
                  </Txt>
                )}
              </View>
            }
            subtitle2={
              todayMissions.length > 0 ? (
                isMissionExpanded ? (
                  <View>
                    {todayMissions.map((mission, index) => (
                      <View key={mission.id}>
                        {index > 0 && <Spacing size={8} />}
                        <Top.SubtitleParagraph color={adaptive.background}>
                          {mission.title}
                          {'\n'}
                          {mission.verifyEnd
                            ? getVerificationMessage(mission.verifyStart, mission.verifyEnd)
                            : '시간 정보 없음'}
                        </Top.SubtitleParagraph>
                      </View>
                    ))}
                  </View>
                ) : (
                  <Top.SubtitleParagraph color={adaptive.background}>
                    {todayMissions[0]?.title} {'\n'}
                    {(() => {
                      const mission = todayMissions[0];
                      if (!mission?.verifyStart || !mission?.verifyEnd) return '시간 정보 없음';
                      return getVerificationMessage(mission.verifyStart, mission.verifyEnd);
                    })()}
                  </Top.SubtitleParagraph>
                )
              ) : (
                <View style={{ minHeight: 44, justifyContent: 'center' }}>
                  <Top.SubtitleParagraph color={adaptive.background}>
                    오늘은 쉬어가는 날입니다{'\n'}
                    다음 미션은 내일 시작됩니다
                  </Top.SubtitleParagraph>
                  {/* 🎉 오늘의 미션을 모두 완료했습니다. 이번 주 목표까지 1회 남아 있어요*/}
                </View>
              )
            }
            right={
              <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
                <Asset.Image
                  frameShape={Asset.frameShape.CleanW60}
                  source={{
                    uri:
                      todayMissions.length > 0
                        ? 'https://static.toss.im/ml-product/typing-laptop-apng.png'
                        : 'https://static.toss.im/ml-product/farmer-golden-rice-plant.png',
                  }}
                />
              </View>
            }
          />
        </View>
      </Pressable>

      <Spacing size={20} />

      <Top
        title={
          <Top.TitleParagraph color={adaptive.grey900}>
            목표를 정하고{'\n'}
            친구들과 달성해보세요!
          </Top.TitleParagraph>
        }
        right={<Top.RightButton onPress={() => navigation.navigate('/create-challenge')}>만들기</Top.RightButton>}
      />

      {showTooltip && todayMissions.length > 0 && (
        <ListRow
          left={<ListRow.Icon name="icon-emoji-money-with-wings" />}
          contents={
            <ListRow.Texts
              type="2RowTypeD"
              top="오늘 미션을 하지 않으면"
              topProps={{ color: adaptive.grey600 }}
              bottom={`${totalPenalty.toLocaleString()}원을 납부해야 돼요`}
              bottomProps={{ color: adaptive.blue500, fontWeight: 'bold' }}
            />
          }
          right={
            <Pressable onPress={() => setShowTooltip(false)}>
              <Icon name="icon-x-mono" color={adaptive.grey600} size={16} />
            </Pressable>
          }
          verticalPadding={16}
        />
      )}

      <ListHeader
        title={
          <ListHeader.TitleSelector
            typography="t4"
            color={adaptive.grey800}
            fontWeight="bold"
            onPress={() => {
              setShowDropdown(!showDropdown);
            }}
          >
            {currentStatus === 'ACTIVE'
              ? '진행중인 챌린지'
              : currentStatus === 'PENDING'
                ? '예정된 챌린지'
                : '완료된 챌린지'}
          </ListHeader.TitleSelector>
        }
      />
      {showDropdown && (
        <View style={styles.dropdownMenu}>
          <Pressable
            style={styles.dropdownItem}
            onPress={() => {
              setCurrentStatus('ACTIVE');
              setShowDropdown(false);
            }}
          >
            <Txt typography="t5" color={currentStatus === 'ACTIVE' ? adaptive.blue500 : adaptive.grey800}>
              진행중인 챌린지
            </Txt>
            {currentStatus === 'ACTIVE' && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
          </Pressable>
          <Pressable
            style={styles.dropdownItem}
            onPress={() => {
              setCurrentStatus('PENDING');
              setShowDropdown(false);
            }}
          >
            <Txt typography="t5" color={currentStatus === 'PENDING' ? adaptive.blue500 : adaptive.grey800}>
              예정된 챌린지
            </Txt>
            {currentStatus === 'PENDING' && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
          </Pressable>
          <Pressable
            style={styles.dropdownItem}
            onPress={() => {
              setCurrentStatus('COMPLETE');
              setShowDropdown(false);
            }}
          >
            <Txt typography="t5" color={currentStatus === 'COMPLETE' ? adaptive.blue500 : adaptive.grey800}>
              완료된 챌린지
            </Txt>
            {currentStatus === 'COMPLETE' && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
          </Pressable>
        </View>
      )}

      {/* 챌린지 카드 반복 렌더링 */}
      {/*TODO : 무한 스크롤 or  페이징 적용*/}
      {challenges.length > 0 ? (
        challenges.map((challenge, index) => (
          <View key={challenge.id || `challenge-${index}`}>
            {index > 0 && <Spacing size={16} />}
            <ChallengeCard challenge={{ ...challenge, status: currentStatus }} />
          </View>
        ))
      ) : (
        <OnboardingView
          adaptive={adaptive}
          onCreateObjective={() => navigation.navigate('/create-challenge')}
          currentStatus={currentStatus}
        />
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
    paddingVertical: 20,
  },
  dropdownMenu: {
    backgroundColor: 'white',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#e5e7eb',
    marginHorizontal: 16,
    marginTop: 8,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  dropdownItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
  },
});
