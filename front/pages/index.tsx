import { createRoute, Spacing } from '@granite-js/react-native';
import { View, StyleSheet, ScrollView, Pressable, Text } from 'react-native';
import { Asset, Top, ListRow, ListHeader, Icon, Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { useState, useEffect, useMemo } from 'react';
import { Storage } from '@apps-in-toss/framework';
import { ChallengeCard } from 'components/challenge/ChallengeCard';
import { getMyProgressChallenges } from '../src/api/challenges';
import { Challenge } from '../src/components/challenge/types';
import { LottieView } from '@granite-js/native/lottie-react-native';

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
      // await Storage.removeItem('hasCompletedOnboarding');

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
  const [challenges, setChallenges] = useState<Challenge[]>([]);
  const [currentStatus, setCurrentStatus] = useState<'progress' | 'pending' | 'complete'>('progress');
  const fetchChallenges = async (status: 'progress' | 'pending' | 'complete') => {
    try {
      const response = await getMyProgressChallenges(1, status);
      if (response.success) {
        const mappedChallenges: Challenge[] = response.data.map((item) => ({
          id: String(item.challengeId),
          title: item.challengeTitle,
          status: status === 'progress' ? 'in_progress' : status === 'pending' ? 'pending' : 'completed',
          currentCount: item.currentWeeklyVerificationCount,
          totalCount: item.weeklyRequiredVerificationCount,
          penaltyAmount: Number(item.penaltyAmount),
          remainingTime: item.remainingTimeFormatted,
          participants: '',
          participantCount: 0,
          deadline: '',
          verificationTime: '',
          verificationFrequency: '',
          verificationMethod: 'PHOTO',
        }));
        setChallenges(mappedChallenges);
      }
    } catch (error) {
      console.error('Failed to fetch challenges:', error);
    }
  };

  useEffect(() => {
    fetchChallenges(currentStatus);
  }, [currentStatus]);

  // 오늘 미션 필터링 (remainingTime이 있는 챌린지)
  const todayMissions = useMemo(
    () => challenges.filter((challenge) => challenge.remainingTime !== undefined && challenge.remainingTime !== null),
    [challenges]
  );

  // 오늘 미션 벌금 합산
  const totalPenalty = useMemo(
    () => todayMissions.reduce((sum, challenge) => sum + challenge.penaltyAmount, 0),
    [todayMissions]
  );

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      {/* 오늘의 미션 */}
      {/*TODO : 로그인 안했을 때, 로그인 했을 때 , 미션이 없을 때로 구분*/}
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
                  <Text style={{ color: adaptive.background, fontSize: 16, fontWeight: 'bold' }}>
                    {isMissionExpanded ? '∨' : '>'}
                  </Text>
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
                          남은 시간 : {mission.remainingTime}
                        </Top.SubtitleParagraph>
                      </View>
                    ))}
                  </View>
                ) : (
                  <Top.SubtitleParagraph color={adaptive.background}>
                    {todayMissions[0]?.title} {'\n'}
                    남은 시간 : {todayMissions[0]?.remainingTime}
                  </Top.SubtitleParagraph>
                )
              ) : (
                <View style={{ minHeight: 44, justifyContent: 'center' }}>
                  <Top.SubtitleParagraph color={adaptive.background}>
                    해야될 미션이 없어요{'\n'}
                    오늘은 푹 쉬어도 좋아요
                  </Top.SubtitleParagraph>
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

      {/* 목표 설정 섹션 */}
      <Top
        title={
          <Top.TitleParagraph color={adaptive.grey900}>
            목표를 정하고{'\n'}
            친구들과 달성해보세요!
          </Top.TitleParagraph>
        }
        right={<Top.RightButton onPress={() => navigation.navigate('/create-goal')}>만들기</Top.RightButton>}
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

      {/* 진행중인 챌린지 헤더 */}
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
            {currentStatus === 'progress'
              ? '진행중인 챌린지'
              : currentStatus === 'pending'
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
              console.log('Progress selected');
              setCurrentStatus('progress');
              setShowDropdown(false);
            }}
          >
            <Txt typography="t5" color={currentStatus === 'progress' ? adaptive.blue500 : adaptive.grey800}>
              진행중인 챌린지
            </Txt>
            {currentStatus === 'progress' && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
          </Pressable>
          <Pressable
            style={styles.dropdownItem}
            onPress={() => {
              console.log('Pending selected');
              setCurrentStatus('pending');
              setShowDropdown(false);
            }}
          >
            <Txt typography="t5" color={currentStatus === 'pending' ? adaptive.blue500 : adaptive.grey800}>
              예정된 챌린지
            </Txt>
            {currentStatus === 'pending' && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
          </Pressable>
          <Pressable
            style={styles.dropdownItem}
            onPress={() => {
              console.log('Complete selected');
              setCurrentStatus('complete');
              setShowDropdown(false);
            }}
          >
            <Txt typography="t5" color={currentStatus === 'complete' ? adaptive.blue500 : adaptive.grey800}>
              완료된 챌린지
            </Txt>
            {currentStatus === 'complete' && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
          </Pressable>
        </View>
      )}

      {/* 챌린지 카드 반복 렌더링 */}
      {/*TODO : 무한 스크롤 or  페이징 적용*/}
      {challenges.length > 0 ? (
        challenges.map((challenge, index) => (
          <View key={challenge.id}>
            {index > 0 && <Spacing size={16} />}
            <ChallengeCard challenge={challenge} />
          </View>
        ))
      ) : (
        <OnboardingView
          adaptive={adaptive}
          onCreateObjective={() => navigation.navigate('/create-goal')}
          currentStatus={currentStatus}
        />
      )}
    </ScrollView>
  );
}

function OnboardingView({
  adaptive,
  onCreateObjective,
  currentStatus,
}: {
  adaptive: any;
  onCreateObjective: () => void;
  currentStatus: 'progress' | 'pending' | 'complete';
}) {
  const getMessage = () => {
    switch (currentStatus) {
      case 'progress':
        return {
          title: '아직 진행 중인 챌린지가 없어요',
          description: '작은 습관 하나가 큰 변화를 만들어요.\n지금 바로 첫 번째 목표를 세워볼까요?',
        };
      case 'pending':
        return {
          title: '예정된 챌린지가 없어요',
          description: '새로운 챌린지를 시작할 준비가 되셨나요?\n목표를 설정하고 시작해보세요!',
        };
      case 'complete':
        return {
          title: '완료된 챌린지가 없어요',
          description: '첫 번째 챌린지를 완료하고\n성취감을 느껴보세요!',
        };
    }
  };

  const message = getMessage();

  return (
    <View style={styles.onboardingContainer}>
      <View style={{ marginTop: -80, marginBottom: -60 }} pointerEvents="none">
        <LottieView
          source={{ uri: 'https://lottie.host/ab39ffda-09a1-44fe-ade6-1236d48e6720/AixS7quFKd.lottie' }}
          autoPlay
          loop
          renderMode="SOFTWARE"
          style={{ width: 200, height: 200 }}
        />
      </View>
      <Txt typography="t5" fontWeight="bold" color={adaptive.grey800} style={{ textAlign: 'center' }}>
        {message.title}
      </Txt>
      <Spacing size={8} />
      <Txt typography="t6" color={adaptive.grey600} style={{ textAlign: 'center' }}>
        {message.description}
      </Txt>
      <Spacing size={12} />
      <ListHeader
        title={<ListHeader.TitleParagraph color={adaptive.grey700}>이런 목표는 어때요?</ListHeader.TitleParagraph>}
      />
      <Spacing size={12} />
      <View style={styles.templateGrid}>
        {['매일 물 2L 마시기', '아침 8시 기상하기', '하루 30분 독서'].map((template) => (
          <Pressable key={template} style={styles.templateCard} onPress={onCreateObjective}>
            <Txt typography="t6" fontWeight="semibold" color={adaptive.grey800}>
              {template}
            </Txt>
            <Icon name="icon-arrow-right-small-mono" color={adaptive.grey400} size={16} />
          </Pressable>
        ))}
      </View>
    </View>
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
  onboardingContainer: {
    paddingHorizontal: 24,
    alignItems: 'center',
    paddingVertical: 40,
  },
  templateGrid: {
    width: '100%',
    gap: 12,
    marginTop: 8,
  },
  templateCard: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#f9fafb',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#f2f4f6',
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
