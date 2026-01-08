import { createRoute, Spacing } from '@granite-js/react-native';
import {
  Top,
  List,
  ListRow,
  FixedBottomCTA,
  FixedBottomCTAProvider,
  Txt,
  ListHeader,
  SegmentedControl,
} from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { Linking, Alert, View, ScrollView } from 'react-native';
import React, { useState, useMemo } from 'react';
import { useChallengeStore } from '../../src/stores/challengeStore';
import { useAllPenalties, useMyPenalties } from '../../src/hooks/usePenalties';
import { Card } from '../../src/components/common/Card';

export const Route = createRoute('/penalty-history', {
  component: Page,
});

// 토스 송금 실행 함수 (기존 유지)
const requestTossPayment = async (amount: number) => {
  // 실제 송금 링크는 수취인 계좌 등이 필요하므로 여기서는 예시로 홈으로 이동하거나 더미 링크 사용
  const url = `supertoss://send?amount=${amount}`;

  try {
    const supported = await Linking.canOpenURL(url);

    if (supported) {
      await Linking.openURL(url);
    } else {
      Alert.alert('알림', '토스 앱이 설치되어 있지 않습니다.');
    }
  } catch (error) {
    console.error('Link 호출 중 오류 발생:', error);
    Alert.alert('오류', '송금 요청 중 문제가 발생했습니다.');
  }
};

export default function Page() {
  const adaptive = useAdaptive();
  const selectedChallenge = useChallengeStore((s) => s.selectedChallengeObject);
  const challengeId = selectedChallenge?.id || null;

  const [tab, setTab] = useState<'ME' | 'ALL'>('ME');

  const { data: myPenalties = [] } = useMyPenalties(challengeId, { enabled: true }); // 내 내역은 기본으로 진입시 로드 (미납액 계산 위해 필수)
  const { data: allPenalties = [] } = useAllPenalties(challengeId, { enabled: tab === 'ALL' }); // 전체 내역은 탭 누를 때만

  const displayPenalties = useMemo(() => {
    return tab === 'ME' ? myPenalties : allPenalties;
  }, [tab, myPenalties, allPenalties]);

  const unpaidTotal = useMemo(() => {
    return myPenalties
      .filter((p) => !p.paid)
      .reduce((sum, p) => sum + p.amount, 0);
  }, [myPenalties]);

  if (!selectedChallenge) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <Txt color={adaptive.grey600}>챌린지 정보를 불러올 수 없습니다.</Txt>
      </View>
    );
  }

  return (
    <FixedBottomCTAProvider>
      <ScrollView style={{ backgroundColor: adaptive.background }}>
        <Top
          title={
            <Top.TitleParagraph color={adaptive.grey900}>
              패널티 이력
            </Top.TitleParagraph>
          }
          subtitle2={
            <Top.SubtitleParagraph color={adaptive.grey600}>
              {selectedChallenge.title}
            </Top.SubtitleParagraph>
          }
        />

        {/* 내 벌금 현황 카드 */}
        <View style={{ padding: 16 }}>
          <Card>
            <Txt typography="t6" color={adaptive.grey600}>
              내 미납 벌금
            </Txt>
            <Spacing size={4} />
            <Txt typography="t3" fontWeight="bold" color={adaptive.grey900}>
              {unpaidTotal.toLocaleString()}원
            </Txt>
          </Card>
        </View>

        <View style={{ paddingHorizontal: 16, paddingBottom: 12 }}>
          <SegmentedControl.Root name="penalty-history-tab" value={tab} onChange={(value) => setTab(value as 'ME' | 'ALL')}>
            <SegmentedControl.Item value="ME">내 내역</SegmentedControl.Item>
            <SegmentedControl.Item value="ALL">전체 내역</SegmentedControl.Item>
          </SegmentedControl.Root>
        </View>

        <ListHeader
          title={
            <ListHeader.TitleParagraph color={adaptive.grey700}>
              {tab === 'ME' ? '내 패널티 내역' : '전체 멤버 패널티 내역'}
            </ListHeader.TitleParagraph>
          }
        />

        <List>
          {displayPenalties.length > 0 ? (
            displayPenalties.map((penalty) => (
              <ListRow
                key={penalty.penaltyId}
                contents={
                  <ListRow.Texts
                    type="2RowTypeB"
                    top={
                      <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                         {tab === 'ALL' && (
                           <Txt color={adaptive.grey900} fontWeight="bold" style={{ marginRight: 6 }}>
                             {penalty.memberName}
                           </Txt>
                         )}
                         <Txt color={adaptive.grey800}>
                           {penalty.paid ? '납부 완료' : '미납'}
                         </Txt>
                      </View>
                    }
                    bottom={penalty.createdAt.split('T')[0]} // YYYY-MM-DD
                    bottomProps={{ color: adaptive.grey500 }}
                  />
                }
                right={
                  <View style={{ alignItems: 'flex-end' }}>
                    <Txt
                      typography="t6"
                      fontWeight="bold"
                      color={!penalty.paid ? adaptive.red500 : adaptive.grey600}
                    >
                      {penalty.amount.toLocaleString()}원
                    </Txt>
                    <Txt
                      typography="t7"
                      color={!penalty.paid ? adaptive.red500 : adaptive.grey400}
                    >
                      {!penalty.paid ? '미납' : '납부완료'}
                    </Txt>
                  </View>
                }
                verticalPadding="small"
              />
            ))
          ) : (
             <View style={{ padding: 24, alignItems: 'center' }}>
               <Txt color={adaptive.grey500}>내역이 없습니다.</Txt>
             </View>
          )}
        </List>
        <Spacing size={40} />
      </ScrollView>

      <FixedBottomCTA
        type="primary"
        style="fill"
        disabled={unpaidTotal === 0}
        onPress={() => requestTossPayment(unpaidTotal)}
      >
        {unpaidTotal > 0
          ? `${unpaidTotal.toLocaleString()}원 토스로 송금하기`
          : '미납된 패널티가 없어요'}
      </FixedBottomCTA>
    </FixedBottomCTAProvider>
  );
}
