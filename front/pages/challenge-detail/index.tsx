import {createRoute, Spacing} from '@granite-js/react-native';
import {Asset, BarChart, FixedBottomCTA, FixedBottomCTAProvider, ListHeader, Top, Txt} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {AuthGuard} from '../../src/components/common/AuthGuard';
import {Pressable, ScrollView, StyleSheet, View} from 'react-native';
import {Card} from '../../src/components/common/Card';
import {VerificationGroup} from '../../src/components/verification/VerificationGroup';
import {useVerificationModal} from '../../src/hooks/useVerificationModal';
import {useMe} from '../../src/hooks/useMe';
import {useLatestVerification, useMemberVerificationCounts} from '../../src/hooks/useVerifications';
import {useChallengeStore} from '../../src/stores/challengeStore';
import {useCreateChallengeStore} from '../../src/stores/createChallengeStore';
import {getChallengeEditForm} from '../../src/api/challenges';

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
  const updateData = useCreateChallengeStore((s) => s.updateData);
  const { data: latestVerification } = useLatestVerification(selectedChallenge?.id || null);
  const {
    data: memberCounts,
    isLoading: isMemberCountsLoading,
    error: memberCountsError,
  } = useMemberVerificationCounts(selectedChallenge?.id || null);

  const { data: me } = useMe();
  
  // 내 Role 확인
  const myRole = selectedChallenge?.members?.find(m => m.tossId === me?.tossId)?.role;
  if (!selectedChallenge) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Txt color={adaptive.grey600}>챌린지 정보를 불러올 수 없습니다.</Txt>
      </View>
    );
  }

  return (
    <AuthGuard>
      <ScrollView>
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>{selectedChallenge.title}</Top.TitleParagraph>}
        subtitle2={
          <Top.SubtitleParagraph color={adaptive.grey600}>
            {(() => {
               if (!selectedChallenge.members || selectedChallenge.members.length <= 1) {
                   return '첫 번째로 도전하고 있어요';
               }
              
               const randomIndex = Math.floor(Math.random() * selectedChallenge.members.length);
               const randomMember = selectedChallenge.members[randomIndex];
               return `${randomMember?.userName} 외 ${selectedChallenge.members.length - 1}명과 함께 도전 중`;
            })()}
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
            {selectedChallenge.status === 'ACTIVE' && selectedChallenge.verifyEnd
              ? getVerificationMessage(selectedChallenge.verifyStart, selectedChallenge.verifyEnd)
              : ''}
          </Top.SubtitleParagraph>
        }
        subtitle2={
          <Top.SubtitleBadges
            items={[
              getChallengeStatusBadge(
                selectedChallenge.verificationStatus,
                selectedChallenge.daysOfWeek,
                selectedChallenge.weeklyRequiredCount,
                selectedChallenge.weeklyProgressCount,
                selectedChallenge.verifyStart,
                selectedChallenge.verifyEnd
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

      {/* 최근 인증 현황 */}
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
      <View style={{ marginBottom: 24 }}>
        {latestVerification ? (
          <VerificationGroup
            date={latestVerification.dateTime}
            verifications={[
              {
                id: latestVerification.id,
                tossId: latestVerification.tossId,
                userName: latestVerification.name,
                imageUrl: latestVerification.image,
                dateTime: latestVerification.dateTime,
              },
            ]}
          />
        ) : (
          <View style={{ paddingHorizontal: 16 }}>
            <Card>
              <View style={styles.emptyCard}>
                <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                  인증 내역이 없습니다.
                </Txt>
              </View>
            </Card>
          </View>
        )}
      </View>

      {/* 주간 인증 현황 */}
      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            주간 인증 현황
          </ListHeader.TitleParagraph>
        }
      />
      <Spacing size={8} />
      <View style={{ paddingHorizontal: 16 }}>
        <Card>
          {memberCountsError ? (
            <View style={styles.emptyCard}>
              <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                인증 현황을 불러올 수 없습니다.
              </Txt>
            </View>
          ) : isMemberCountsLoading ? (
            <View style={styles.emptyCard}>
              <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                로딩중...
              </Txt>
            </View>
          ) : Array.isArray(memberCounts) &&
            memberCounts.length > 0 &&
            memberCounts.some((m) => m.verificationCount && m.verificationCount > 0) ? (
            <BarChart
              data={memberCounts.map((member) => ({
                xAxisLabel: member.userName,
                value: Number(member.verificationCount ?? 0),
              }))}
              fill={{ type: 'all-bar', theme: 'blue' }}
            />
          ) : (
            <View style={styles.emptyCard}>
              <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                멤버별 인증 현황 데이터가 없습니다.
              </Txt>
            </View>
          )}
        </Card>
      </View>
      

      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            패널티 이력 보기
          </ListHeader.TitleParagraph>
        }
        right={
          <Pressable onPress={() => navigation.navigate('/challenge-detail/penalty-history')}>
            <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
              자세히 보기
            </ListHeader.RightArrow>
          </Pressable>
        }
      />

      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            참여중인 친구
          </ListHeader.TitleParagraph>
        }
        right={
          myRole === 'CREATOR' ? (
            <Pressable onPress={() => navigation.navigate('/challenge-detail/manage-members')}>
              <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
                친구 초대/관리
              </ListHeader.RightArrow>
            </Pressable>
          ) : (
            <Pressable onPress={() => navigation.navigate('/challenge-detail/manage-members')}>
              <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
                친구 보기 / 나가기
              </ListHeader.RightArrow>
            </Pressable>
          )
        }
      />

      {/* 챌린지 규칙 */}
      <ListHeader
        title={
          <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
            챌린저 규칙
          </ListHeader.TitleParagraph>
        }
        right={
          myRole === 'CREATOR' ? (
            <Pressable
              onPress={async () => {
                try {
                  const editForm = await getChallengeEditForm(selectedChallenge.id.toString());
                  const dayMapping: { [key: string]: string } = {
                    MON: '월',
                    TUE: '화',
                    WED: '수',
                    THU: '목',
                    FRI: '금',
                    SAT: '토',
                    SUN: '일',
                  };

                  const isDayType = editForm.daysOfWeek && editForm.daysOfWeek.length > 0;
                  const periodValue = isDayType
                    ? editForm.daysOfWeek!.map((d: string) => dayMapping[d] || d).join(', ')
                    : `${editForm.frequency}회`;

                  updateData({
                    isEditing: true,
                    challengeId: selectedChallenge.id.toString(),
                    title: editForm.title,
                    verificationType: editForm.verificationType,
                    penaltyAmount: editForm.penaltyAmount,
                    endDate: editForm.endDate,
                    verifyStartAt: editForm.verifyStartAt ?? undefined,
                    verifyEndAt: editForm.verifyEndAt ?? undefined,
                    daysOfWeek: editForm.daysOfWeek || [],
                    frequency: editForm.frequency || 0,
                    period: periodValue,
                    startDate: isDayType ? 'day' : 'count',
                  });

                  navigation.navigate('/create-challenge/step2');
                } catch (error) {
                  console.error('수정 데이터 로드 실패:', error);
                  alert('챌린지 정보를 불러오지 못했습니다.');
                }
              }}
            >
              <ListHeader.RightArrow typography="t7" color={adaptive.grey600}>
                수정하기
              </ListHeader.RightArrow>
            </Pressable>
          ) : undefined
        }
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

      <FixedBottomCTAProvider>
        <FixedBottomCTA loading={false} onPress={selectedChallenge ? openVerificationModal : undefined}>
          바로 인증하기
        </FixedBottomCTA>
      </FixedBottomCTAProvider>
      </ScrollView>
    </AuthGuard>
  );
}

const styles = StyleSheet.create({
  emptyCard: {
    minHeight: 180,
    justifyContent: 'center',
    alignItems: 'center',
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
