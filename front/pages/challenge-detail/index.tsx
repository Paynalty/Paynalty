import { createRoute, Spacing } from '@granite-js/react-native';
import { Asset, BarChart, FixedBottomCTA, FixedBottomCTAProvider, ListHeader, Top, Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { Pressable, ScrollView, StyleSheet, View } from 'react-native';
import { VerificationGroup } from '../../src/components/verification/VerificationGroup';
import { useVerificationModal } from '../../src/hooks/useVerificationModal';
import { useLatestVerification, useMemberVerificationCounts } from '../../src/hooks/useVerifications';
import { useChallengeStore } from '../../src/stores/challengeStore';
import {
  formatDate,
  formatDaysOfWeek,
  formatTime,
  getChallengeStatusBadge,
  getVerificationMessage,
  getVerificationTypeLabel,
} from '../../src/utils/challenge';

export const Route = createRoute('/challenge-detail', {
  component: Page,
});

function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const selectedChallenge = useChallengeStore((s) => s.selectedChallengeObject);
  const { open: openVerificationModal } = useVerificationModal();
  const { data: latestVerification } = useLatestVerification(selectedChallenge?.id || '');
  const {
    data: memberCounts,
    isLoading: isMemberCountsLoading,
    error: memberCountsError,
  } = useMemberVerificationCounts(selectedChallenge?.id || '');

  if (!selectedChallenge) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Txt color={adaptive.grey600}>챌린지 정보를 불러올 수 없습니다.</Txt>
      </View>
    );
  }

  return (
    <ScrollView>
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>{selectedChallenge.title}</Top.TitleParagraph>}
        subtitle2={
          <Top.SubtitleParagraph color={adaptive.grey600}>
            {`${selectedChallenge.participants} ${selectedChallenge.participantCount}명과 도전 중`}
          </Top.SubtitleParagraph>
        }
        right={
          <Top.UpperAssetContent
            content={
              <Asset.Image
                frameShape={Asset.frameShape.CleanW60}
                source={{
                  uri: 'https://static.toss.im/ml-product/observer-binocular.png',
                }}
              />
            }
          />
        }
        lowerGap={0}
      />
      <Top
        title=""
        subtitle1={
          <Top.SubtitleParagraph>
            {selectedChallenge.verifyEnd
              ? getVerificationMessage(selectedChallenge.verifyStart, selectedChallenge.verifyEnd)
              : '시간 정보 없음'}
          </Top.SubtitleParagraph>
        }
        subtitle2={
          <Top.SubtitleBadges
            items={[
              getChallengeStatusBadge(
                selectedChallenge.verificationStatus,
                selectedChallenge.daysOfWeek,
                Number(selectedChallenge.weeklyRequiredCount),
                selectedChallenge.weeklyProgressCount
              ),
              {
                label: `${selectedChallenge.weeklyProgressCount}/${selectedChallenge.weeklyRequiredCount}`,
                type: (selectedChallenge.verificationStatus === 'VERIFIED' ? 'green' : 'yellow') as any,
                style: 'weak' as const,
              },
              {
                label: `${selectedChallenge.penaltyAmount.toLocaleString()}원`,
                type: 'blue',
                style: 'weak',
              },
            ]}
          />
        }
        upperGap={0}
      />
      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            최근 인증 현황
          </ListHeader.TitleParagraph>
        }
        right={
          <Pressable onPress={() => navigation.navigate('/challenge-detail/verification-history')}>
            <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
              자세히 보기
            </ListHeader.RightArrow>
          </Pressable>
        }
      />
      {latestVerification ? (
        <VerificationGroup
          date={latestVerification.dateTime}
          verifications={[
            {
              id: latestVerification.id,
              userName: latestVerification.name,
              imageUrl: latestVerification.image,
              dateTime: latestVerification.dateTime,
            },
          ]}
        />
      ) : (
        <View style={[styles.verificationCard, styles.emptyCard]}>
          <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
            인증 내역이 없습니다.
          </Txt>
        </View>
      )}
      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            주간 인증 현황
          </ListHeader.TitleParagraph>
        }
        /*right={
                  <ListHeader.TitleSelector typography="t7" color={adaptive.grey800} fontWeight="regular">
                    보기 기준
                  </ListHeader.TitleSelector>
                }*/
      />
      {memberCountsError ? (
        <View style={[styles.verificationCard, styles.emptyCard]}>
          <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
            인증 현황을 불러올 수 없습니다.
          </Txt>
        </View>
      ) : isMemberCountsLoading ? (
        <View style={[styles.verificationCard, styles.emptyCard]}>
          <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
            로딩중...
          </Txt>
        </View>
      ) : Array.isArray(memberCounts) &&
        memberCounts.length > 0 &&
        memberCounts.some((m) => m.verificationCount && m.verificationCount > 0) ? (
        <BarChart
          data={memberCounts.map((member) => {
            const value = member.verificationCount ?? 0;
            return {
              xAxisLabel: member.userName,
              value: Number(value),
            };
          })}
          fill={{ type: 'all-bar', theme: 'blue' }}
        />
      ) : (
        <View style={[styles.verificationCard, styles.emptyCard]}>
          <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
            멤버별 인증 현황 데이터가 없습니다.
          </Txt>
        </View>
      )}
      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            챌린저 규칙
          </ListHeader.TitleParagraph>
        }
        /*right={
                  <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
                    수정하기
                  </ListHeader.RightArrow>
                }*/
      />
      <View style={styles.rulesCard}>
        <View style={styles.gridCell}>
          <Asset.Icon
            frameShape={{ width: 28, height: 28 }}
            name="icon-calendar-gradient-mono"
            color={adaptive.grey600}
          />
          <Txt color={adaptive.grey600} typography="t6" fontWeight="medium">
            마감일
          </Txt>
          <Txt color={adaptive.grey900} typography="t5" fontWeight="bold">
            {formatDate(selectedChallenge.endAt)}
          </Txt>
        </View>
        <View style={styles.gridRow}>
          <View style={styles.gridCell}>
            <Asset.Icon frameShape={{ width: 24, height: 24 }} name="icon-clock-mono" color={adaptive.grey600} />
            <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
              인증 시간
            </Txt>
            <Txt color={adaptive.grey900} typography="t6" fontWeight="semibold">
              {formatTime(selectedChallenge.verifyStart)} ~ {formatTime(selectedChallenge.verifyEnd)}
            </Txt>
          </View>
          <View style={styles.gridDivider} />
          <View style={styles.gridCell}>
            <Asset.Icon frameShape={{ width: 24, height: 24 }} name="icon-repeat-mono" color={adaptive.grey600} />
            <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
              인증 주기
            </Txt>
            <Txt color={adaptive.grey900} typography="t6" fontWeight="semibold">
              {selectedChallenge.daysOfWeek && selectedChallenge.daysOfWeek.length > 0
                ? `${formatDaysOfWeek(selectedChallenge.daysOfWeek)} / `
                : ''}
              주 {selectedChallenge.weeklyRequiredCount}회
            </Txt>
          </View>
        </View>
        <View style={styles.gridHorizontalDivider} />
        <View style={styles.gridRow}>
          <View style={styles.gridCell}>
            <Asset.Icon frameShape={{ width: 24, height: 24 }} name="icon-check-circle-mono" color={adaptive.grey600} />
            <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
              인증 방법
            </Txt>
            <Txt color={adaptive.grey900} typography="t6" fontWeight="semibold">
              {getVerificationTypeLabel(selectedChallenge.verificationType)}
            </Txt>
          </View>
          <View style={styles.gridDivider} />
          <View style={styles.gridCell}>
            <Asset.Icon frameShape={{ width: 24, height: 24 }} name="icon-won-mono" color={adaptive.grey600} />
            <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
              패널티
            </Txt>
            <Txt color={adaptive.grey900} typography="t5" fontWeight="bold">
              {`${selectedChallenge.penaltyAmount.toLocaleString()}원`}
            </Txt>
          </View>
        </View>
      </View>
      {/*<ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            패널티 이력 보기
          </ListHeader.TitleParagraph>
        }
        right={
          <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
            자세히 보기
          </ListHeader.RightArrow>
        }
      />*/}
      {/*<ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            참여중인 친구
          </ListHeader.TitleParagraph>
        }
        right={
          <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
            자세히 보기
          </ListHeader.RightArrow>
        }
      />*/}
      <FixedBottomCTAProvider>
        <FixedBottomCTA loading={false} onPress={openVerificationModal}>
          바로 인증하기
        </FixedBottomCTA>
      </FixedBottomCTAProvider>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  verificationCard: {
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 8,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  emptyCard: {
    minHeight: 180,
    justifyContent: 'center',
    alignItems: 'center',
  },
  dateSection: {
    alignItems: 'center',
    marginBottom: 12,
  },
  userSection: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginBottom: 16,
  },
  imageSection: {
    alignItems: 'center',
    marginVertical: 16,
  },
  bottomSection: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 12,
  },
  rulesCard: {
    padding: 0,
    marginHorizontal: 16,
    marginVertical: 8,
    overflow: 'hidden',
  },
  gridRow: {
    flexDirection: 'row',
    alignItems: 'stretch',
  },
  gridCell: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 20,
    paddingHorizontal: 12,
    gap: 6,
  },
  gridDivider: {
    width: 1,
  },
  gridHorizontalDivider: {
    height: 1,
  },
});
