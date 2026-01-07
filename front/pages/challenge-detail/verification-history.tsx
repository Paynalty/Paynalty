import { Asset, Txt, Top, ListHeader } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { ScrollView, View } from 'react-native';
import { VerificationGroup } from '../../src/components/verification/VerificationGroup';
import { useVerifications } from '../../src/hooks/useVerifications';
import { useChallengeStore } from '../../src/stores/challengeStore';
import { getChallengeStatusBadge, getVerificationMessage } from '../../src/utils/challenge';
import { ChallengeVerificationResponse } from '../../src/api/verifications';
import { AuthGuard } from '../../src/components/common/AuthGuard';

export default function Page() {
  const adaptive = useAdaptive();
  const selectedChallenge = useChallengeStore((s) => s.selectedChallengeObject);
  const challengeId = selectedChallenge?.id ? String(selectedChallenge.id) : '';
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } = useVerifications(challengeId);

  // 무한 쿼리 데이터를 단일 배열로 평탄화
  const verifications = data?.pages?.flatMap((page) => page.content) ?? [];

  if (!selectedChallenge) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Txt color={adaptive.grey600}>챌린지 정보를 불러올 수 없습니다.</Txt>
      </View>
    );
  }

  // 데이터를 날짜별로 그룹화 (YYYY-MM-DD 기준)
  const groupedVerifications = (verifications as ChallengeVerificationResponse[]).reduce(
    (acc: { date: string; items: ChallengeVerificationResponse[] }[], current) => {
      const dateKey = current.dateTime.split('T')[0];
      const existingGroup = acc.find((group) => group.date.startsWith(dateKey || ''));

      if (existingGroup) {
        existingGroup.items.push(current);
      } else {
        acc.push({
          date: current.dateTime,
          items: [current],
        });
      }
      return acc;
    },
    []
  );

  const handleScroll = (event: any) => {
    const { layoutMeasurement, contentOffset, contentSize } = event.nativeEvent;
    const isCloseToBottom = layoutMeasurement.height + contentOffset.y >= contentSize.height - 50;

    if (isCloseToBottom && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  };

  return (
    <AuthGuard>
      <View style={{ flex: 1 }}>
        <ScrollView onScroll={handleScroll} scrollEventThrottle={16}>
          <Top
            title={<Top.TitleParagraph color={adaptive.grey900}>{selectedChallenge.title}</Top.TitleParagraph>}
            subtitle2={
              <Top.SubtitleParagraph color={adaptive.grey600}>
                {`${selectedChallenge.participants || ''} ${selectedChallenge.participantCount}명과 도전 중`}
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
                {selectedChallenge.verifyStart && selectedChallenge.verifyEnd
                  ? getVerificationMessage(selectedChallenge.verifyStart, selectedChallenge.verifyEnd)
                  : '시간 정보 없음'}
              </Top.SubtitleParagraph>
            }
            subtitle2={
              <Top.SubtitleBadges
                items={[
                  getChallengeStatusBadge(
                    selectedChallenge.verificationStatus,
                    selectedChallenge.daysOfWeek || [],
                    Number(selectedChallenge.weeklyRequiredCount || 0),
                    selectedChallenge.weeklyProgressCount || 0
                  ),
                  {
                    label: `${selectedChallenge.weeklyProgressCount}/${selectedChallenge.weeklyRequiredCount}`,
                    type: (selectedChallenge.verificationStatus === 'VERIFIED' ? 'green' : 'yellow') as any,
                    style: 'weak' as const,
                  },
                  { label: `${(selectedChallenge.penaltyAmount || 0).toLocaleString()}원`, type: 'blue', style: 'weak' },
                ]}
              />
            }
            upperGap={0}
          />
          <ListHeader
            title={
              <ListHeader.TitleParagraph color={adaptive.grey800} fontWeight="bold" typography="t5">
                인증 현황
              </ListHeader.TitleParagraph>
            }
          />

          {groupedVerifications.length > 0 ? (
            groupedVerifications.map((group) => (
              <VerificationGroup key={group.date} date={group.date} verifications={group.items} />
            ))
          ) : (
            <View style={{ padding: 40, alignItems: 'center' }}>
              <Txt color={adaptive.grey500}>아직 인증 내역이 없습니다.</Txt>
            </View>
          )}

          {isFetchingNextPage && (
            <View style={{ padding: 20, alignItems: 'center' }}>
              <Txt color={adaptive.grey600}>더 불러오는 중...</Txt>
            </View>
          )}

          <View style={{ height: 32 }} />
        </ScrollView>
      </View>
    </AuthGuard>
  );
}
