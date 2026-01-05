import { Asset, Top, Txt, ListRow, Icon } from '@toss/tds-react-native';
import { Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import React, { useState, useMemo } from 'react';
import {
  View,
  StyleSheet,
  ScrollView,
  Dimensions,
  Pressable,
  LayoutAnimation,
  NativeSyntheticEvent,
  NativeScrollEvent,
  Platform,
  UIManager,
} from 'react-native';
import { getVerificationMessage, getTimeDate, sortChallengesByPriority } from '../../utils/challenge';

if (Platform.OS === 'android' && UIManager.setLayoutAnimationEnabledExperimental) {
  UIManager.setLayoutAnimationEnabledExperimental(true);
}

interface Mission {
  id: string;
  title: string;
  verificationStatus: 'VERIFIED' | 'NOT_VERIFIED';
  verifyStart: string;
  verifyEnd: string;
  penaltyAmount: number;
}

type CardItem = { type: 'SUMMARY' } | { type: 'CREATE'; action: () => void };

interface Props {
  missions: Mission[];
  onCreateChallenge: () => void;
}

const { width: SCREEN_WIDTH } = Dimensions.get('window');

export function MissionSection({ missions, onCreateChallenge }: Props) {
  const adaptive = useAdaptive();
  const [activeIndex, setActiveIndex] = useState(0);
  const [isExpanded, setIsExpanded] = useState(false);

  const carouselData = useMemo<CardItem[]>(() => {
    const data: CardItem[] = [{ type: 'SUMMARY' }, { type: 'CREATE', action: onCreateChallenge }];
    return data;
  }, [onCreateChallenge]);

  const handleScroll = (event: NativeSyntheticEvent<NativeScrollEvent>) => {
    const scrollOffset = event.nativeEvent.contentOffset.x;
    const index = Math.round(scrollOffset / SCREEN_WIDTH);
    const item = carouselData[index];

    if (item && index !== activeIndex && index >= 0 && index < carouselData.length) {
      setActiveIndex(index);

      // 'CREATE' 카드로 이동할 때 확장을 해제합니다.
      if (item.type === 'CREATE') {
        LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
        setIsExpanded(false);
      }
    }
  };

  const toggleExpand = () => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setIsExpanded(!isExpanded);
  };

  if (carouselData.length === 0) {
    return null;
  }

  const verifiedCount = missions.filter((m) => m.verificationStatus === 'VERIFIED').length;
  const isAllVerified = missions.length > 0 && verifiedCount === missions.length;

  const renderCard = (item: CardItem) => {
    if (item.type === 'SUMMARY') {
      return (
        <Pressable onPress={toggleExpand} style={styles.cardContent}>
          <Top
            title={<Top.TitleParagraph color={adaptive.background}>오늘의 미션</Top.TitleParagraph>}
            subtitle2={
              <View>
                <Txt typography="t5" fontWeight="bold" color={adaptive.background}>
                  {missions.length > 0
                    ? isAllVerified
                      ? '오늘 미션을 모두 완료했어요!'
                      : `${missions.length}개 중 ${verifiedCount}개 완료`
                    : '오늘 예정된 미션이 없어요'}
                </Txt>
                <Spacing size={4} />
                <Txt typography="t7" color="rgba(255, 255, 255, 0.8)">
                  {missions.length > 0
                    ? isAllVerified
                      ? '정말 멋져요! 내일도 함께해요'
                      : '미션을 터치해서 목록을 확인하세요'
                    : '새로운 도전을 시작해보세요'}
                </Txt>
              </View>
            }
            right={
              <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
                <Asset.Image
                  frameShape={Asset.frameShape.CleanW60}
                  source={{
                    uri: isAllVerified
                      ? 'https://static.toss.im/ml-product/farmer-golden-rice-plant.png'
                      : 'https://static.toss.im/ml-product/typing-laptop-apng.png',
                  }}
                />
              </View>
            }
          />
          {isExpanded && missions.length > 0 && (
            <View style={styles.expandedContent}>
              <View style={styles.divider} />
              <Spacing size={12} />
              {sortChallengesByPriority(missions).map((mission) => {
                const isVerified = mission.verificationStatus === 'VERIFIED';
                const now = new Date().getTime();
                const start = mission.verifyStart ? getTimeDate(mission.verifyStart).getTime() : 0;
                const end = mission.verifyEnd ? getTimeDate(mission.verifyEnd).getTime() : 0;

                let status: 'VERIFIED' | 'IN_PROGRESS' | 'WAITING' = 'WAITING';
                if (isVerified) {
                  status = 'VERIFIED';
                } else if (now >= start && now <= end) {
                  status = 'IN_PROGRESS';
                } else if (now < start) {
                  status = 'WAITING';
                }

                const getStatusStyles = () => {
                  switch (status) {
                    case 'VERIFIED':
                      return {
                        bg: adaptive.background,
                        icon: 'icon-check-bold',
                        iconColor: adaptive.blue500,
                      };
                    case 'IN_PROGRESS':
                      return {
                        bg: adaptive.yellow500, // TDS Yellow
                        icon: 'icon-clock-mono',
                        iconColor: adaptive.background,
                      };
                    default:
                      return {
                        bg: 'rgba(255, 255, 255, 0.2)', // 반투명은 유지하되 TDS 배경색 기반으로 고려 가능하나 현재 맥락 유지
                        icon: 'icon-clock-mono',
                        iconColor: adaptive.background,
                      };
                  }
                };

                const { bg, icon, iconColor } = getStatusStyles();

                return (
                  <ListRow
                    key={mission.id}
                    left={
                      <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                        <View style={[styles.iconCircle, { backgroundColor: bg }]}>
                          <Icon name={icon as any} color={iconColor} size={16} />
                        </View>
                        <Spacing direction="horizontal" size={12} />
                      </View>
                    }
                    contents={
                      <ListRow.Texts
                        type="2RowTypeD"
                        top={mission.title}
                        topProps={{ color: adaptive.background, typography: 't6', fontWeight: 'bold' }}
                        bottom={
                          isVerified
                            ? '인증 완료!'
                            : mission.verifyEnd
                              ? `${getVerificationMessage(mission.verifyStart, mission.verifyEnd)}`
                              : '인증 대기 중'
                        }
                        bottomProps={{ color: 'rgba(255, 255, 255, 0.7)', typography: 't7' }}
                      />
                    }
                    right={
                      !isVerified && (
                        <Txt typography="t7" color={adaptive.background} fontWeight="bold">
                          {mission.penaltyAmount.toLocaleString()}원
                        </Txt>
                      )
                    }
                    verticalPadding="small"
                  />
                );
              })}
            </View>
          )}
        </Pressable>
      );
    }

    if (item.type === 'CREATE') {
      return (
        <View style={styles.cardContent}>
          <Top
            title={<Top.TitleParagraph color={adaptive.background}>새로운 도전</Top.TitleParagraph>}
            subtitle2={
              <View>
                <Txt typography="t5" fontWeight="bold" color={adaptive.background}>
                  목표를 더 만들어볼까요?
                </Txt>
                <Spacing size={4} />
                <Txt typography="t7" color="rgba(255, 255, 255, 0.8)">
                  친구들과 함께하면 더 재밌어요
                </Txt>
              </View>
            }
            right={
              <View>
                <Pressable
                  onPress={item.action}
                  style={({ pressed }) => [styles.actionButton, { opacity: pressed ? 0.6 : 1 }]}
                >
                  <Txt typography="t6" fontWeight="bold" color={adaptive.background}>
                    도전하기
                  </Txt>
                </Pressable>
              </View>
            }
          />
          <Spacing size={12} />
        </View>
      );
    }

    return null;
  };

  const styles = useMemo(
    () =>
      StyleSheet.create({
        container: {
          borderBottomLeftRadius: 24,
          borderBottomRightRadius: 24,
          overflow: 'hidden',
          paddingVertical: 10,
        },
        cardContent: {
          width: SCREEN_WIDTH,
          paddingBottom: 10,
        },
        penaltyBox: {
          backgroundColor: adaptive.background,
          borderRadius: 16,
          overflow: 'hidden',
        },
        expandedContent: {
          paddingHorizontal: 12,
          paddingBottom: 20,
        },
        divider: {
          height: 1,
          backgroundColor: 'rgba(255, 255, 255, 0.2)',
          marginHorizontal: 12,
        },
        indicatorContainer: {
          flexDirection: 'row',
          justifyContent: 'center',
          alignItems: 'center',
          gap: 6,
          marginTop: -10,
        },
        dot: {
          width: 6,
          height: 6,
          borderRadius: 3,
        },
        actionButton: {
          paddingVertical: 10,
          paddingHorizontal: 16,
          borderRadius: 12,
          alignItems: 'center',
          justifyContent: 'center',
          alignSelf: 'flex-start',
          backgroundColor: 'rgba(255, 255, 255, 0.2)',
        },
        iconCircle: {
          width: 32,
          height: 32,
          borderRadius: 16,
          alignItems: 'center',
          justifyContent: 'center',
        },
      }),
    [adaptive]
  );

  return (
    <View style={[styles.container, { backgroundColor: adaptive.blue500 }]}>
      <ScrollView
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        decelerationRate="fast"
      >
        {carouselData.map((item, index) => (
          <View key={`${item.type}-${index}`} style={{ width: SCREEN_WIDTH }}>
            {renderCard(item)}
          </View>
        ))}
      </ScrollView>

      {/* 도트 인디케이터 */}
      {carouselData.length > 1 && (
        <>
          <View style={styles.indicatorContainer}>
            {carouselData.map((_, index) => (
              <View
                key={index}
                style={[
                  styles.dot,
                  {
                    backgroundColor: activeIndex === index ? adaptive.background : 'rgba(255, 255, 255, 0.4)',
                  },
                ]}
              />
            ))}
          </View>
          <Spacing size={16} />
        </>
      )}
    </View>
  );
}
